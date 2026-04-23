package com.cryptoterm.backend.service.strategy;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Стратегия для получения агрегированных данных по потреблению энергии.
 * Вычисляет среднее потребление энергии устройств за заданный период (в ваттах).
 */
@Component
public class PowerConsumptionMetricStrategy implements MetricStrategy {
    
    private final JdbcTemplate jdbcTemplate;
    
    public PowerConsumptionMetricStrategy(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Override
    public List<AggregatedMetricPoint> getAggregatedByDevice(UUID deviceId, OffsetDateTime from, OffsetDateTime to, String bucket) {
        MetricSourceResolver.Source src = MetricSourceResolver.resolve(from, to);
        String sql = "SELECT time_bucket(?::interval, " + src.timeColumn() + ") AS bucket, " +
                "avg(" + src.powerColumn() + ") AS avg_value " +
                "FROM " + src.table() + " " +
                "WHERE device_id = ? AND " + src.timeColumn() + " BETWEEN ? AND ? " +
                "AND " + src.powerColumn() + " IS NOT NULL " +
                "GROUP BY bucket ORDER BY bucket";
        
        return jdbcTemplate.query(sql, ps -> {
            ps.setString(1, bucket);
            ps.setObject(2, deviceId);
            ps.setObject(3, from);
            ps.setObject(4, to);
        }, (rs, rowNum) -> new AggregatedMetricPoint(
                rs.getObject("bucket", OffsetDateTime.class),
                rs.getBigDecimal("avg_value")
        ));
    }
    
    @Override
    public List<AggregatedMetricPoint> getAggregatedByUser(UUID userId, OffsetDateTime from, OffsetDateTime to, String bucket) {
        MetricSourceResolver.Source src = MetricSourceResolver.resolve(from, to);
        String sql = "SELECT time_bucket(?::interval, m." + src.timeColumn() + ") AS bucket, " +
                "avg(m." + src.powerColumn() + ") AS avg_value " +
                "FROM " + src.table() + " m " +
                "JOIN device d ON m.device_id = d.id " +
                "WHERE d.owner_id = ? AND m." + src.timeColumn() + " BETWEEN ? AND ? " +
                "AND m." + src.powerColumn() + " IS NOT NULL " +
                "GROUP BY bucket ORDER BY bucket";
        
        return jdbcTemplate.query(sql, ps -> {
            ps.setString(1, bucket);
            ps.setObject(2, userId);
            ps.setObject(3, from);
            ps.setObject(4, to);
        }, (rs, rowNum) -> new AggregatedMetricPoint(
                rs.getObject("bucket", OffsetDateTime.class),
                rs.getBigDecimal("avg_value")
        ));
    }
    
    @Override
    public String getMetricName() {
        return "power-consumption";
    }
}
