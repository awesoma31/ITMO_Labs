package com.cryptoterm.backend.mqtt;

import com.cryptoterm.backend.device.domain.*;
import com.cryptoterm.backend.monitoring.domain.*;
import com.cryptoterm.backend.command.domain.AsicHttpProxyCommand;
import com.cryptoterm.backend.mqtt.dto.CommandResponseMessage;
import com.cryptoterm.backend.mqtt.dto.LogMessage;
import com.cryptoterm.backend.mqtt.dto.MetricMessage;
import com.cryptoterm.backend.mqtt.dto.OtherMetricMessage;
import com.cryptoterm.backend.mqtt.dto.RegistrationMessage;
import com.cryptoterm.backend.device.application.port.out.DeviceRepository;
import com.cryptoterm.backend.device.application.port.out.MinerRepository;
import com.cryptoterm.backend.monitoring.application.port.out.MetricRepository;
import com.cryptoterm.backend.monitoring.application.port.out.DeviceLogRepository;
import com.cryptoterm.backend.monitoring.application.port.out.MetricTypeRepository;
import com.cryptoterm.backend.monitoring.application.port.out.OtherMetricRepository;
import com.cryptoterm.backend.service.AsicCommandService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

@Component
public class MqttInboundHandler {
    private static final Logger log = LoggerFactory.getLogger(MqttInboundHandler.class);
    
    private final ObjectMapper objectMapper;
    private final MetricRepository metricRepository;
    private final DeviceLogRepository deviceLogRepository;
    private final DeviceRepository deviceRepository;
    private final MinerRepository minerRepository;
    private final MetricTypeRepository metricTypeRepository;
    private final OtherMetricRepository otherMetricRepository;
    private final AsicCommandService commandService;

    public MqttInboundHandler(ObjectMapper objectMapper,
                              MetricRepository metricRepository,
                              DeviceLogRepository deviceLogRepository,
                              DeviceRepository deviceRepository,
                              MinerRepository minerRepository,
                              MetricTypeRepository metricTypeRepository,
                              OtherMetricRepository otherMetricRepository,
                              AsicCommandService commandService) {
        this.objectMapper = objectMapper;
        this.metricRepository = metricRepository;
        this.deviceLogRepository = deviceLogRepository;
        this.deviceRepository = deviceRepository;
        this.minerRepository = minerRepository;
        this.metricTypeRepository = metricTypeRepository;
        this.otherMetricRepository = otherMetricRepository;
        this.commandService = commandService;
    }

    @ServiceActivator(inputChannel = "mqttInboundChannel")
    public void handle(Message<?> message, @Header("mqtt_receivedTopic") String topic) {
        try {
            Object payload = message.getPayload();
            String payloadString;

            if (payload instanceof byte[]) {
                payloadString = new String((byte[]) payload, StandardCharsets.UTF_8);
            } else if (payload instanceof String) {
                payloadString = (String) payload;
            } else {
                log.warn("Unexpected payload type: {}", payload.getClass().getName());
                return;
            }

            log.debug("Processing message on topic {}: {}", topic, payloadString);
            
            processMessage(topic, payloadString);
        } catch (Exception e) {
            log.error("Fatal error handling MQTT message on topic {}: {}", topic, e.getMessage(), e);
            // Don't rethrow - acknowledge message to prevent redelivery accumulation
        }
    }
    
    private void processMessage(String topic, String payloadString) throws Exception {

        if (Topics.METRICS.equals(topic)) {
            MetricMessage mm = objectMapper.readValue(payloadString, MetricMessage.class);
            if (mm.deviceId == null) {
                log.warn("Received metric message with null deviceId, ignoring");
                return;
            }
            Device device = deviceRepository.findById(mm.deviceId).orElse(null);
            if (device == null) {
                log.warn("Device not found: {}", mm.deviceId);
                return;
            }
            Miner miner = null;
            if (mm.instanceId != null) {
                miner = minerRepository.findById(mm.instanceId).orElse(null);
            }
            
            // Конвертация hashrate из GH/s в TH/s если необходимо
            BigDecimal hashRateThs = mm.hashRateThs;
            if (hashRateThs != null && "GH/s".equalsIgnoreCase(mm.hashrateUnit)) {
                hashRateThs = hashRateThs.divide(BigDecimal.valueOf(1000), 2, java.math.RoundingMode.HALF_UP);
                log.debug("Converted hashrate from {} GH/s to {} TH/s", mm.hashRateThs, hashRateThs);
            }
            
            Metric metric = new Metric();
            metric.setDevice(device);
            metric.setMiner(miner);
            
            // Generate unique timestamp by adding nanoseconds to avoid collisions
            OffsetDateTime metricTime = mm.timestamp != null ? 
                OffsetDateTime.ofInstant(Instant.ofEpochMilli(mm.timestamp), ZoneOffset.UTC) : 
                OffsetDateTime.now(ZoneOffset.UTC);
            
            // Add nanosecond precision to minimize collision risk
            metricTime = metricTime.plusNanos(System.nanoTime() % 1_000_000);
            metric.setTime(metricTime);
            
            metric.setTemperatureC(mm.temperatureC);
            metric.setHashRateThs(hashRateThs);
            metric.setPowerConsumptionW(mm.powerConsumptionW);
            metricRepository.save(metric);

        } else if (Topics.OTHER_METRICS.equals(topic)) {

            OtherMetricMessage omm = objectMapper.readValue(payloadString, OtherMetricMessage.class);

            if (omm.deviceId == null) {
                log.warn("Received other metric message with null deviceId, ignoring");
                return;
            }

            Optional<Device> deviceOpt = deviceRepository.findById(omm.deviceId);
            if (deviceOpt.isEmpty()) {
                log.warn("Other metric ignored: device not found: {}", omm.deviceId);
                return;
            }
            Device device = deviceOpt.get();

            if (omm.metricType == null || omm.metricType.isBlank()) {
                log.warn("Other metric ignored: metricType is null/empty from device {}", omm.deviceId);
                return;
            }

            Optional<MetricType> metricTypeOpt = metricTypeRepository.findByName(omm.metricType);
            if (metricTypeOpt.isEmpty()) {
                log.warn("Other metric ignored: unknown metric type '{}' from device {}. "
                        + "Register it via POST /api/metric-types first.", omm.metricType, omm.deviceId);
                return;
            }
            MetricType metricType = metricTypeOpt.get();

            if (!metricType.isActive()) {
                log.debug("Other metric ignored: metric type '{}' is deactivated", omm.metricType);
                return;
            }

            if (omm.metricValue == null) {
                log.warn("Other metric ignored: null value for type '{}' from device {}", omm.metricType, omm.deviceId);
                return;
            }

            UUID minerId = null;
            if (omm.minerId != null) {
                minerId = minerRepository.findById(omm.minerId)
                        .map(Miner::getId)
                        .orElse(null);
            }

            String sensorKey = omm.metricKey != null ? omm.metricKey : "default";

            OffsetDateTime time = omm.timestamp != null
                    ? OffsetDateTime.ofInstant(Instant.ofEpochMilli(omm.timestamp), ZoneOffset.UTC)
                    : OffsetDateTime.now(ZoneOffset.UTC);
            time = time.plusNanos(System.nanoTime() % 1_000_000);

            OtherMetricId id = new OtherMetricId(time, metricType.getId(), device.getId(), sensorKey);
            OtherMetric metric = new OtherMetric(id, minerId, omm.metricValue);

            otherMetricRepository.save(metric);
        } else if (Topics.LOGS.equals(topic)) {
            try {
                LogMessage lm = objectMapper.readValue(payloadString, LogMessage.class);
                log.debug("Parsed log message - deviceId: {}, level: {}, message: {}", 
                    lm.deviceId, lm.level, lm.message);
                
                if (lm.deviceId == null) {
                    log.warn("Received log message with null deviceId, raw payload: {}", payloadString);
                    return;
                }
                
                // FILTER OUT DEBUG LOGS - they fill up disk space
                // Only store INFO, WARN, ERROR logs in database
                if ("DEBUG".equalsIgnoreCase(lm.level)) {
                    log.trace("Skipping DEBUG log from device {}: {}", lm.deviceId, lm.message);
                    return;
                }
                
                Optional<Device> deviceOpt = deviceRepository.findById(lm.deviceId);
                if (deviceOpt.isEmpty()) {
                    log.warn("Device not found for id: {}", lm.deviceId);
                    return;
                }
                
                Device device = deviceOpt.get();
                DeviceLog dl = new DeviceLog();
                dl.setDevice(device);
                dl.setLevel(lm.level);
                dl.setMessage(lm.message);
                
                // Generate unique timestamp by adding nanoseconds to avoid collisions
                OffsetDateTime logTime = lm.timestamp != null ? 
                    OffsetDateTime.ofInstant(Instant.ofEpochMilli(lm.timestamp), ZoneOffset.UTC) : 
                    OffsetDateTime.now(ZoneOffset.UTC);
                
                // Add nanosecond precision to minimize collision risk
                logTime = logTime.plusNanos(System.nanoTime() % 1_000_000);
                dl.setTime(logTime);
                
                deviceLogRepository.save(dl);
            } catch (Exception e) {
                log.error("Failed to process log message: {}", payloadString, e);
            }

        } else if (topic.startsWith("devices/") && topic.endsWith("/responses")) {
            // Handle command responses from devices
            handleCommandResponse(payloadString);
        }
    }
    
    /**
     * Handle command response from a Raspberry Pi device.
     * The response contains the execution result of an ASIC HTTP Proxy command.
     */
    private void handleCommandResponse(String payloadString) throws Exception {
        try {
            CommandResponseMessage response = objectMapper.readValue(
                payloadString, CommandResponseMessage.class);
            
            log.info("Received command response for cmdId: {}, status: {}", 
                response.getCmdId(), response.getStatus());
            
            // Convert DTO to domain result
            AsicHttpProxyCommand.CommandResult result = new AsicHttpProxyCommand.CommandResult();
            result.setStatus(response.getStatus());
            result.setFailedStep(response.getFailedStep());
            
            if (response.getStepResults() != null) {
                var stepResults = response.getStepResults().stream()
                    .map(sr -> {
                        AsicHttpProxyCommand.StepResult stepResult = 
                            new AsicHttpProxyCommand.StepResult();
                        stepResult.setStepId(sr.getStepId());
                        stepResult.setStatus(sr.getStatus());
                        stepResult.setStatusCode(sr.getStatusCode());
                        stepResult.setResponsePreview(sr.getResponsePreview());
                        stepResult.setError(sr.getError());
                        return stepResult;
                    })
                    .toList();
                result.setStepResults(stepResults);
            }
            
            // Update command in database
            commandService.updateResult(response.getCmdId(), result);
            
        } catch (Exception e) {
            log.error("Failed to process command response: {}", e.getMessage(), e);
            throw e; // Re-throw to be caught by outer handler
        }
    }
}


