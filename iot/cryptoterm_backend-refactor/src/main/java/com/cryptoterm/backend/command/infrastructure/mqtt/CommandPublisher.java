package com.cryptoterm.backend.mqtt;

import com.cryptoterm.backend.command.domain.AsicHttpProxyCommand;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Publisher для отправки команд на устройства через MQTT.
 * 
 * Этот сервис отправляет ASIC HTTP Proxy команды на устройства Raspberry Pi,
 * которые затем выполняют HTTP запросы к ASIC майнерам.
 */
@Component
public class CommandPublisher {
    private static final Logger log = LoggerFactory.getLogger(CommandPublisher.class);
    private final MessageChannel mqttOutboundChannel;
    private final ObjectMapper objectMapper;

    public CommandPublisher(MessageChannel mqttOutboundChannel, ObjectMapper objectMapper) {
        this.mqttOutboundChannel = mqttOutboundChannel;
        this.objectMapper = objectMapper;
    }

    /**
     * Отправить ASIC HTTP Proxy команду на устройство.
     * Команда будет перенаправлена на указанное устройство через MQTT.
     * RP определит IP адрес на основе minerId и подставит переменные ${address} и ${port}.
     * 
     * @param command ASIC HTTP Proxy команда для отправки
     * @return true если сообщение отправлено успешно, false в противном случае
     */
    public boolean sendAsicProxyCommand(AsicHttpProxyCommand command) {
        try {
            // Построить payload команды
            Map<String, Object> payload = new HashMap<>();
            payload.put("deviceId", command.getDeviceId());
            payload.put("command", "asic_http_proxy");
            payload.put("cmdId", command.getCmdId());
            
            // Добавить информацию об ASIC с шаблонными переменными
            Map<String, Object> asicInfo = new HashMap<>();
            
            // Добавить minerId как id внутри asic объекта
            if (command.getMinerId() != null) {
                asicInfo.put("id", command.getMinerId().toString());
            }
            
            // RP подставит значения для этих переменных
            asicInfo.put("ip", "${address}");  // RP подставит IP по minerId
            asicInfo.put("port", "${port}");    // RP подставит порт из конфига
            
            // Добавить остальные поля из AsicConnectionInfo
            if (command.getAsic() != null) {
                if (command.getAsic().getFirmware() != null) {
                    asicInfo.put("firmware", command.getAsic().getFirmware());
                }
                if (command.getAsic().getScheme() != null) {
                    asicInfo.put("scheme", command.getAsic().getScheme());
                }
                // Если port указан явно в команде, используем его (иначе ${port})
                if (command.getAsic().getPort() != null) {
                    asicInfo.put("port", command.getAsic().getPort());
                }
            }
            
            payload.put("asic", asicInfo);
            payload.put("steps", command.getSteps());
            
            if (command.getPolicy() != null) {
                payload.put("policy", command.getPolicy());
            }
            
            if (command.getSignature() != null && !command.getSignature().isEmpty()) {
                payload.put("signature", command.getSignature());
            }

            String jsonPayload = objectMapper.writeValueAsString(payload);
            String topic = Topics.getDeviceCommandTopic(command.getDeviceId());

            boolean sent = mqttOutboundChannel.send(
                    MessageBuilder.withPayload(jsonPayload)
                            .setHeader("mqtt_topic", topic)
                            .build()
            );
            
            if (sent) {
                log.info("Отправлена ASIC proxy команда {} на устройство {} (майнер: {}) на топик {}", 
                    command.getCmdId(), command.getDeviceId(), command.getMinerId(), topic);
            } else {
                log.error("Не удалось отправить ASIC proxy команду {} на устройство {} (майнер: {})", 
                    command.getCmdId(), command.getDeviceId(), command.getMinerId());
            }
            
            return sent;
            
        } catch (JsonProcessingException e) {
            log.error("Не удалось сериализовать ASIC proxy команду {}", command.getCmdId(), e);
            return false;
        }
    }
}

