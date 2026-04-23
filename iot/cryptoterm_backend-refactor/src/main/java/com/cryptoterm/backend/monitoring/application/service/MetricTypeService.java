package com.cryptoterm.backend.service;

import com.cryptoterm.backend.monitoring.application.port.out.MetricTypeRepository;
import com.cryptoterm.backend.monitoring.domain.MetricType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MetricTypeService {

    private final MetricTypeRepository metricTypeRepository;
    private final JdbcTemplate jdbcTemplate;

    public MetricTypeService(MetricTypeRepository metricTypeRepository, JdbcTemplate jdbcTemplate) {
        this.metricTypeRepository = metricTypeRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public MetricType create(String name, String unit, String description, String displayName) {
        if (name == null || !name.matches("^[a-z0-9_]+$")) {
            throw new IllegalArgumentException("Name must match [a-z0-9_]+ pattern");
        }
        if (metricTypeRepository.existsByName(name)) {
            throw new IllegalArgumentException("Metric type already exists: " + name);
        }
        if (unit == null || unit.isBlank()) {
            throw new IllegalArgumentException("Unit is required");
        }

        MetricType mt = new MetricType(name, unit, description);
        mt.setDisplayName(displayName);
        return metricTypeRepository.save(mt);
    }

    public List<MetricType> findAllActive() {
        return metricTypeRepository.findByActiveTrue();
    }

    public MetricType findById(Integer id) {
        return metricTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Metric type not found: " + id));
    }

    @Transactional
    public MetricType update(Integer id, String unit, String description, String displayName) {
        MetricType mt = findById(id);
        if (unit != null && !unit.isBlank()) {
            mt.setUnit(unit);
        }
        if (description != null) {
            mt.setDescription(description);
        }
        if (displayName != null) {
            mt.setDisplayName(displayName);
        }
        return metricTypeRepository.save(mt);
    }

    @Transactional
    public void deactivate(Integer id) {
        MetricType mt = findById(id);
        mt.setActive(false);
        metricTypeRepository.save(mt);
    }

    public MetricTypeStats getStats(Integer id) {
        MetricType mt = findById(id);
        String sql = """
            SELECT COUNT(*) AS total_records,
                   MIN(time) AS first_record,
                   MAX(time) AS last_record
            FROM other_metric
            WHERE metric_type_id = ?
            """;
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new MetricTypeStats(
                mt,
                rs.getLong("total_records"),
                rs.getTimestamp("first_record"),
                rs.getTimestamp("last_record")
        ), id);
    }

    public record MetricTypeStats(
            MetricType metricType,
            long totalRecords,
            java.sql.Timestamp firstRecord,
            java.sql.Timestamp lastRecord
    ) {}
}
