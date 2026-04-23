package com.cryptoterm.backend.service.strategy;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class OtherMetricStrategy implements MetricStrategy {

    private final JdbcTemplate jdbcTemplate;

    public OtherMetricStrategy(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public record OtherAggregatedPoint(
            OffsetDateTime time,
            BigDecimal value,
            String sensorKey
    ) {}

    public record LatestMetricPoint(
            String metricName,
            String displayName,
            String unit,
            String sensorKey,
            Double value,
            OffsetDateTime time
    ) {}

    public record SensorInfo(
            String sensorKey,
            String metricName,
            String displayName,
            String unit,
            OffsetDateTime lastSeen
    ) {}

    @Override
    public List<AggregatedMetricPoint> getAggregatedByDevice(
            UUID deviceId, OffsetDateTime from, OffsetDateTime to, String bucket) {
        return getAggregatedByDeviceAndType(deviceId, "ambient_temperature", from, to, bucket)
                .stream()
                .map(p -> new AggregatedMetricPoint(p.time(), p.value()))
                .toList();
    }

    public List<OtherAggregatedPoint> getAggregatedByDeviceAndType(
            UUID deviceId, String metricName,
            OffsetDateTime from, OffsetDateTime to, String bucket) {
        String sql = """
            SELECT time_bucket(?::interval, om.time) AS bucket,
                   om.sensor_key,
                   avg(om.value) AS avg_value
            FROM other_metric om
            JOIN metric_type mt ON mt.id = om.metric_type_id
            WHERE om.device_id = ?
              AND mt.name = ?
              AND mt.is_active = true
              AND om.time BETWEEN ? AND ?
            GROUP BY bucket, om.sensor_key
            ORDER BY bucket, om.sensor_key
            """;

        return jdbcTemplate.query(sql, ps -> {
            ps.setString(1, bucket);
            ps.setObject(2, deviceId);
            ps.setString(3, metricName);
            ps.setObject(4, from);
            ps.setObject(5, to);
        }, (rs, rowNum) -> new OtherAggregatedPoint(
                rs.getObject("bucket", OffsetDateTime.class),
                rs.getBigDecimal("avg_value"),
                rs.getString("sensor_key")
        ));
    }

    public List<LatestMetricPoint> getLatestByDevice(UUID deviceId) {
        String sql = """
            SELECT DISTINCT ON (mt.name, om.sensor_key)
                   mt.name AS metric_name,
                   mt.display_name,
                   mt.unit,
                   om.sensor_key,
                   om.value,
                   om.time
            FROM other_metric om
            JOIN metric_type mt ON mt.id = om.metric_type_id
            WHERE om.device_id = ?
              AND mt.is_active = true
            ORDER BY mt.name, om.sensor_key, om.time DESC
            """;

        return jdbcTemplate.query(sql, ps -> {
            ps.setObject(1, deviceId);
        }, (rs, rowNum) -> new LatestMetricPoint(
                rs.getString("metric_name"),
                rs.getString("display_name"),
                rs.getString("unit"),
                rs.getString("sensor_key"),
                rs.getDouble("value"),
                rs.getObject("time", OffsetDateTime.class)
        ));
    }

    public List<SensorInfo> getSensorsByDevice(UUID deviceId) {
        String sql = """
            SELECT om.sensor_key,
                   mt.name AS metric_name,
                   mt.display_name,
                   mt.unit,
                   MAX(om.time) AS last_seen
            FROM other_metric om
            JOIN metric_type mt ON mt.id = om.metric_type_id
            WHERE om.device_id = ?
              AND mt.is_active = true
            GROUP BY om.sensor_key, mt.name, mt.display_name, mt.unit
            ORDER BY mt.name, om.sensor_key
            """;

        return jdbcTemplate.query(sql, ps -> {
            ps.setObject(1, deviceId);
        }, (rs, rowNum) -> new SensorInfo(
                rs.getString("sensor_key"),
                rs.getString("metric_name"),
                rs.getString("display_name"),
                rs.getString("unit"),
                rs.getObject("last_seen", OffsetDateTime.class)
        ));
    }

    @Override
    public List<AggregatedMetricPoint> getAggregatedByUser(UUID userId, OffsetDateTime from, OffsetDateTime to, String bucket) {
        throw new UnsupportedOperationException("Use specific methods for other metrics");
    }

    @Override
    public String getMetricName() {
        return "other";
    }
}