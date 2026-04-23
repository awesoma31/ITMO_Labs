package com.cryptoterm.backend.service;

import com.cryptoterm.backend.device.domain.Device;
import com.cryptoterm.backend.monitoring.domain.Metric;
import com.cryptoterm.backend.device.domain.Miner;
import com.cryptoterm.backend.dto.MetricDto;
import com.cryptoterm.backend.device.application.port.out.DeviceRepository;
import com.cryptoterm.backend.monitoring.application.port.out.MetricRepository;
import com.cryptoterm.backend.service.strategy.MetricSourceResolver;
import com.cryptoterm.backend.service.strategy.MetricStrategy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сервис для работы с метриками устройств.
 * Использует паттерн стратегия для работы с разными типами метрик.
 */
@Service
public class MetricsService {
    private final MetricRepository metricRepository;
    private final DeviceRepository deviceRepository;
    private final JdbcTemplate jdbcTemplate;

    public MetricsService(MetricRepository metricRepository, DeviceRepository deviceRepository, JdbcTemplate jdbcTemplate) {
        this.metricRepository = metricRepository;
        this.deviceRepository = deviceRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public record AggregatedPoint(OffsetDateTime time, BigDecimal avgTemp, BigDecimal avgHashRate) {}

    /**
     * Получить raw метрики для устройства.
     *
     * @param deviceId ID устройства
     * @param from начальная дата
     * @param to конечная дата
     * @return список метрик
     */
    public List<MetricDto> getRawByDevice(UUID deviceId, OffsetDateTime from, OffsetDateTime to) {
        List<Metric> metrics = metricRepository.findByDevice_IdAndTimeBetween(deviceId, from, to);
        return metrics.stream()
                .filter(metric -> {
                    Miner miner = metric.getMiner();
                    return miner != null && miner.getId() != null;
                })
                .map(metric -> new MetricDto(
                        metric.getTime(),
                        metric.getMiner().getId(),
                        metric.getTemperatureC(),
                        metric.getHashRateThs()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Получить агрегированные метрики (температура и хэшрейт) для устройства.
     * Legacy метод для обратной совместимости.
     *
     * @param deviceId ID устройства
     * @param from начальная дата
     * @param to конечная дата
     * @param bucket интервал агрегации
     * @return список агрегированных точек
     */
    public List<AggregatedPoint> getAggregatedByDevice(UUID deviceId, OffsetDateTime from, OffsetDateTime to, String bucket) {
        MetricSourceResolver.Source src = MetricSourceResolver.resolve(from, to);
        String sql = "SELECT time_bucket(?::interval, " + src.timeColumn() + ") AS bucket, " +
                "avg(" + src.temperatureColumn() + ") AS avg_temp, " +
                "avg(" + src.hashRateColumn() + ") AS avg_hr " +
                "FROM " + src.table() + " " +
                "WHERE device_id = ? AND " + src.timeColumn() + " BETWEEN ? AND ? " +
                "GROUP BY bucket ORDER BY bucket";
        return jdbcTemplate.query(sql, ps -> {
            ps.setString(1, bucket);
            ps.setObject(2, deviceId);
            ps.setObject(3, from);
            ps.setObject(4, to);
        }, (rs, rowNum) -> new AggregatedPoint(
                rs.getObject("bucket", OffsetDateTime.class),
                rs.getBigDecimal("avg_temp"),
                rs.getBigDecimal("avg_hr")
        ));
    }

    /**
     * Получить агрегированные метрики используя стратегию для устройства.
     *
     * @param strategy стратегия метрики
     * @param deviceId ID устройства
     * @param from начальная дата
     * @param to конечная дата
     * @param bucket интервал агрегации
     * @return список агрегированных точек
     */
    public List<MetricStrategy.AggregatedMetricPoint> getAggregatedMetricsByDevice(
            MetricStrategy strategy, UUID deviceId, OffsetDateTime from, OffsetDateTime to, String bucket) {
        return strategy.getAggregatedByDevice(deviceId, from, to, bucket);
    }

    /**
     * Получить агрегированные метрики используя стратегию для всех устройств пользователя.
     *
     * @param strategy стратегия метрики
     * @param userId ID пользователя
     * @param from начальная дата
     * @param to конечная дата
     * @param bucket интервал агрегации
     * @return список агрегированных точек
     */
    public List<MetricStrategy.AggregatedMetricPoint> getAggregatedMetricsByUser(
            MetricStrategy strategy, UUID userId, OffsetDateTime from, OffsetDateTime to, String bucket) {
        return strategy.getAggregatedByUser(userId, from, to, bucket);
    }

    /**
     * Проверить доступ пользователя к устройству.
     *
     * @param deviceId ID устройства
     * @param userId ID пользователя
     * @param isAdmin является ли пользователь администратором
     * @throws SecurityException если доступ запрещен
     * @throws IllegalArgumentException если устройство не найдено
     */
    public void checkDeviceAccess(UUID deviceId, UUID userId, boolean isAdmin) {
        if (isAdmin) {
            return;
        }
        
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("Device not found"));
        
        if (!device.getOwner().getId().equals(userId)) {
            throw new SecurityException("Access denied to device");
        }
    }
}


