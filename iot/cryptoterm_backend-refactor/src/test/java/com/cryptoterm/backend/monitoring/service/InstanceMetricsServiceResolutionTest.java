package com.cryptoterm.backend.monitoring.service;

import com.cryptoterm.backend.device.application.port.out.MinerRepository;
import com.cryptoterm.backend.device.application.port.out.TemperatureSensorRepository;
import com.cryptoterm.backend.monitoring.application.port.out.MetricRepository;
import com.cryptoterm.backend.service.InstanceMetricsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstanceMetricsServiceResolutionTest {

    @Mock private MetricRepository metricRepository;
    @Mock private MinerRepository minerRepository;
    @Mock private TemperatureSensorRepository temperatureSensorRepository;
    @Mock private JdbcTemplate jdbcTemplate;

    @Captor private ArgumentCaptor<String> sqlCaptor;

    private InstanceMetricsService service;

    private static final OffsetDateTime NOW = OffsetDateTime.now(ZoneOffset.UTC);
    private static final UUID DEVICE_ID = UUID.randomUUID();
    private static final UUID INSTANCE_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new InstanceMetricsService(metricRepository, minerRepository, temperatureSensorRepository, jdbcTemplate);

        when(jdbcTemplate.query(anyString(), any(PreparedStatementSetter.class), any(RowMapper.class)))
                .thenReturn(Collections.emptyList());
    }

    @Test
    void getAggregatedMetrics_recentRange_queriesRawTable() {
        service.getAggregatedMetrics(DEVICE_ID, INSTANCE_ID, NOW.minusHours(12), NOW, "5 minutes");

        verify(jdbcTemplate).query(sqlCaptor.capture(), any(PreparedStatementSetter.class), any(RowMapper.class));
        String sql = sqlCaptor.getValue();

        assertTrue(sql.contains("FROM metric "), "Should query raw table for short range");
        assertTrue(sql.contains("temperature_c"), "Should use raw temperature column");
        assertTrue(sql.contains("hash_rate_ths"), "Should use raw hashrate column");
        assertTrue(sql.contains("power_consumption_w"), "Should use raw power column");
    }

    @Test
    void getAggregatedMetrics_weekRange_queriesHourlyAggregate() {
        service.getAggregatedMetrics(DEVICE_ID, INSTANCE_ID, NOW.minusDays(7), NOW, "1 hour");

        verify(jdbcTemplate).query(sqlCaptor.capture(), any(PreparedStatementSetter.class), any(RowMapper.class));
        String sql = sqlCaptor.getValue();

        assertTrue(sql.contains("FROM metric_1h "), "Should query hourly aggregate for week range");
        assertTrue(sql.contains("temperature_avg"), "Should use aggregate temperature column");
        assertTrue(sql.contains("hash_rate_avg"), "Should use aggregate hashrate column");
        assertTrue(sql.contains("power_consumption_avg"), "Should use aggregate power column");
    }

    @Test
    void getAggregatedMetrics_yearRange_queriesDailyAggregate() {
        service.getAggregatedMetrics(DEVICE_ID, INSTANCE_ID, NOW.minusDays(365), NOW, "1 day");

        verify(jdbcTemplate).query(sqlCaptor.capture(), any(PreparedStatementSetter.class), any(RowMapper.class));
        String sql = sqlCaptor.getValue();

        assertTrue(sql.contains("FROM metric_1d "), "Should query daily aggregate for year range");
        assertTrue(sql.contains("temperature_avg"), "Should use aggregate column");
    }

    @Test
    void getAggregatedMetrics_allRanges_includeDeviceAndMinerFilter() {
        for (int days : new int[]{1, 7, 90}) {
            service.getAggregatedMetrics(DEVICE_ID, INSTANCE_ID, NOW.minusDays(days), NOW, "1 hour");
        }

        verify(jdbcTemplate, times(3)).query(sqlCaptor.capture(), any(PreparedStatementSetter.class), any(RowMapper.class));

        for (String sql : sqlCaptor.getAllValues()) {
            assertTrue(sql.contains("device_id"), "All queries must filter by device_id");
            assertTrue(sql.contains("miner_id"), "All queries must filter by miner_id");
        }
    }
}
