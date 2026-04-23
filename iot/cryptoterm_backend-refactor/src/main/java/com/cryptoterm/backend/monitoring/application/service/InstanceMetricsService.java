package com.cryptoterm.backend.service;

import com.cryptoterm.backend.device.domain.Miner;
import com.cryptoterm.backend.device.domain.TemperatureSensor;
import com.cryptoterm.backend.device.application.port.out.MinerRepository;
import com.cryptoterm.backend.device.application.port.out.TemperatureSensorRepository;
import com.cryptoterm.backend.monitoring.application.port.out.MetricRepository;
import com.cryptoterm.backend.monitoring.domain.Metric;
import com.cryptoterm.backend.service.strategy.MetricSourceResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сервис для работы с метриками instances (ASIC или датчики).
 */
@Service
public class InstanceMetricsService {
    
    private final MetricRepository metricRepository;
    private final MinerRepository minerRepository;
    private final TemperatureSensorRepository temperatureSensorRepository;
    private final JdbcTemplate jdbcTemplate;

    public InstanceMetricsService(MetricRepository metricRepository,
                                 MinerRepository minerRepository,
                                 TemperatureSensorRepository temperatureSensorRepository,
                                 JdbcTemplate jdbcTemplate) {
        this.metricRepository = metricRepository;
        this.minerRepository = minerRepository;
        this.temperatureSensorRepository = temperatureSensorRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Определить тип instance (ASIC или датчик).
     */
    public InstanceType getInstanceType(UUID instanceId) {
        Optional<Miner> miner = minerRepository.findById(instanceId);
        if (miner.isPresent()) {
            return InstanceType.ASIC;
        }
        
        Optional<TemperatureSensor> sensor = temperatureSensorRepository.findById(instanceId);
        if (sensor.isPresent()) {
            return InstanceType.TEMPERATURE_SENSOR;
        }
        
        return InstanceType.UNKNOWN;
    }

    /**
     * Получить информацию об instance.
     */
    public InstanceInfo getInstanceInfo(UUID instanceId) {
        Optional<Miner> miner = minerRepository.findById(instanceId);
        if (miner.isPresent()) {
            Miner m = miner.get();
            return new InstanceInfo(
                instanceId,
                InstanceType.ASIC,
                m.getLabel(),
                m.getDevice().getId()
            );
        }
        
        Optional<TemperatureSensor> sensor = temperatureSensorRepository.findById(instanceId);
        if (sensor.isPresent()) {
            TemperatureSensor s = sensor.get();
            return new InstanceInfo(
                instanceId,
                InstanceType.TEMPERATURE_SENSOR,
                s.getName(),
                s.getDevice() != null ? s.getDevice().getId() : null
            );
        }
        
        throw new IllegalArgumentException("Instance not found: " + instanceId);
    }

    /**
     * Получить все instances для устройства.
     */
    public List<InstanceInfo> getDeviceInstances(UUID deviceId) {
        List<InstanceInfo> instances = new java.util.ArrayList<>();
        
        // Добавляем все ASIC-и
        List<Miner> miners = minerRepository.findByDevice_Id(deviceId);
        instances.addAll(miners.stream()
            .map(m -> new InstanceInfo(m.getId(), InstanceType.ASIC, m.getLabel(), deviceId))
            .collect(Collectors.toList()));
        
        // Добавляем все датчики напрямую по device_id
        String sql = "SELECT id, name FROM temperature_sensor WHERE device_id = ?";
        
        jdbcTemplate.query(sql, ps -> ps.setObject(1, deviceId), (rs, rowNum) -> {
            InstanceInfo info = new InstanceInfo(
                rs.getObject("id", UUID.class),
                InstanceType.TEMPERATURE_SENSOR,
                rs.getString("name"),
                deviceId
            );
            instances.add(info);
            return null;
        });
        
        return instances;
    }

    /**
     * Получить агрегированные метрики для instance.
     */
    public List<AggregatedInstanceMetric> getAggregatedMetrics(
            UUID deviceId, UUID instanceId, OffsetDateTime from, OffsetDateTime to, String bucket) {
        
        MetricSourceResolver.Source src = MetricSourceResolver.resolve(from, to);
        String sql = "SELECT time_bucket(?::interval, " + src.timeColumn() + ") AS bucket, " +
                    "avg(" + src.temperatureColumn() + ") AS avg_temp, " +
                    "avg(" + src.hashRateColumn() + ") AS avg_hashrate, " +
                    "avg(" + src.powerColumn() + ") AS avg_power " +
                    "FROM " + src.table() + " " +
                    "WHERE device_id = ? AND miner_id = ? AND " + src.timeColumn() + " BETWEEN ? AND ? " +
                    "GROUP BY bucket ORDER BY bucket";
        
        return jdbcTemplate.query(sql, ps -> {
            ps.setString(1, bucket);
            ps.setObject(2, deviceId);
            ps.setObject(3, instanceId);
            ps.setObject(4, from);
            ps.setObject(5, to);
        }, (rs, rowNum) -> new AggregatedInstanceMetric(
            rs.getObject("bucket", OffsetDateTime.class),
            rs.getBigDecimal("avg_temp"),
            rs.getBigDecimal("avg_hashrate"),
            rs.getBigDecimal("avg_power")
        ));
    }

    public enum InstanceType {
        ASIC,
        TEMPERATURE_SENSOR,
        UNKNOWN
    }

    public record InstanceInfo(
        UUID instanceId,
        InstanceType type,
        String name,
        UUID deviceId
    ) {}

    public record AggregatedInstanceMetric(
        OffsetDateTime time,
        BigDecimal avgTemperature,
        BigDecimal avgHashRate,
        BigDecimal avgPowerConsumption
    ) {}
}
