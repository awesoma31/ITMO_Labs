package com.cryptoterm.backend.monitoring.strategy;

import com.cryptoterm.backend.service.strategy.HashRateMetricStrategy;
import com.cryptoterm.backend.service.strategy.MetricStrategy.AggregatedMetricPoint;
import com.cryptoterm.backend.service.strategy.PowerConsumptionMetricStrategy;
import com.cryptoterm.backend.service.strategy.TemperatureMetricStrategy;
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

/**
 * Verifies that each metric strategy selects the correct source table
 * (raw vs hourly vs daily aggregate) based on the requested time range.
 */
@ExtendWith(MockitoExtension.class)
class MetricStrategyResolutionTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Captor
    private ArgumentCaptor<String> sqlCaptor;

    private TemperatureMetricStrategy temperatureStrategy;
    private HashRateMetricStrategy hashRateStrategy;
    private PowerConsumptionMetricStrategy powerStrategy;

    private static final OffsetDateTime NOW = OffsetDateTime.now(ZoneOffset.UTC);
    private static final UUID DEVICE_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String BUCKET = "5 minutes";

    @BeforeEach
    void setUp() {
        temperatureStrategy = new TemperatureMetricStrategy(jdbcTemplate);
        hashRateStrategy = new HashRateMetricStrategy(jdbcTemplate);
        powerStrategy = new PowerConsumptionMetricStrategy(jdbcTemplate);

        when(jdbcTemplate.query(anyString(), any(PreparedStatementSetter.class), any(RowMapper.class)))
                .thenReturn(Collections.emptyList());
    }

    // --- Temperature Strategy ---

    @Test
    void temperature_recentRange_queriesRawTable() {
        temperatureStrategy.getAggregatedByDevice(DEVICE_ID, NOW.minusHours(12), NOW, BUCKET);

        verify(jdbcTemplate).query(sqlCaptor.capture(), any(PreparedStatementSetter.class), any(RowMapper.class));
        String sql = sqlCaptor.getValue();
        assertTrue(sql.contains("FROM metric "), "Should query raw 'metric' table");
        assertTrue(sql.contains("temperature_c"), "Should use raw column name");
    }

    @Test
    void temperature_weekRange_queriesHourlyAggregate() {
        temperatureStrategy.getAggregatedByDevice(DEVICE_ID, NOW.minusDays(7), NOW, BUCKET);

        verify(jdbcTemplate).query(sqlCaptor.capture(), any(PreparedStatementSetter.class), any(RowMapper.class));
        String sql = sqlCaptor.getValue();
        assertTrue(sql.contains("FROM metric_1h "), "Should query hourly aggregate");
        assertTrue(sql.contains("temperature_avg"), "Should use aggregate column name");
        assertTrue(sql.contains("bucket"), "Should use 'bucket' as time column");
    }

    @Test
    void temperature_yearRange_queriesDailyAggregate() {
        temperatureStrategy.getAggregatedByDevice(DEVICE_ID, NOW.minusDays(365), NOW, BUCKET);

        verify(jdbcTemplate).query(sqlCaptor.capture(), any(PreparedStatementSetter.class), any(RowMapper.class));
        String sql = sqlCaptor.getValue();
        assertTrue(sql.contains("FROM metric_1d "), "Should query daily aggregate");
    }

    @Test
    void temperature_byUser_weekRange_queriesHourlyAggregate() {
        temperatureStrategy.getAggregatedByUser(USER_ID, NOW.minusDays(7), NOW, BUCKET);

        verify(jdbcTemplate).query(sqlCaptor.capture(), any(PreparedStatementSetter.class), any(RowMapper.class));
        String sql = sqlCaptor.getValue();
        assertTrue(sql.contains("FROM metric_1h m"), "Should query hourly aggregate with alias");
        assertTrue(sql.contains("JOIN device"), "Should join with device table");
    }

    // --- HashRate Strategy ---

    @Test
    void hashRate_recentRange_queriesRawTable() {
        hashRateStrategy.getAggregatedByDevice(DEVICE_ID, NOW.minusHours(6), NOW, BUCKET);

        verify(jdbcTemplate).query(sqlCaptor.capture(), any(PreparedStatementSetter.class), any(RowMapper.class));
        String sql = sqlCaptor.getValue();
        assertTrue(sql.contains("FROM metric "), "Should query raw table");
        assertTrue(sql.contains("hash_rate_ths"), "Should use raw column");
    }

    @Test
    void hashRate_monthRange_queriesHourlyAggregate() {
        hashRateStrategy.getAggregatedByDevice(DEVICE_ID, NOW.minusDays(14), NOW, BUCKET);

        verify(jdbcTemplate).query(sqlCaptor.capture(), any(PreparedStatementSetter.class), any(RowMapper.class));
        String sql = sqlCaptor.getValue();
        assertTrue(sql.contains("FROM metric_1h "), "Should query hourly aggregate");
        assertTrue(sql.contains("hash_rate_avg"), "Should use aggregate column");
    }

    @Test
    void hashRate_yearRange_queriesDailyAggregate() {
        hashRateStrategy.getAggregatedByDevice(DEVICE_ID, NOW.minusDays(90), NOW, BUCKET);

        verify(jdbcTemplate).query(sqlCaptor.capture(), any(PreparedStatementSetter.class), any(RowMapper.class));
        String sql = sqlCaptor.getValue();
        assertTrue(sql.contains("FROM metric_1d "), "Should query daily aggregate");
        assertTrue(sql.contains("hash_rate_avg"), "Should use aggregate column");
    }

    // --- PowerConsumption Strategy ---

    @Test
    void power_recentRange_queriesRawTable() {
        powerStrategy.getAggregatedByDevice(DEVICE_ID, NOW.minusDays(1), NOW, BUCKET);

        verify(jdbcTemplate).query(sqlCaptor.capture(), any(PreparedStatementSetter.class), any(RowMapper.class));
        String sql = sqlCaptor.getValue();
        assertTrue(sql.contains("FROM metric "), "Should query raw table");
        assertTrue(sql.contains("power_consumption_w"), "Should use raw column");
    }

    @Test
    void power_weekRange_queriesHourlyAggregate() {
        powerStrategy.getAggregatedByDevice(DEVICE_ID, NOW.minusDays(7), NOW, BUCKET);

        verify(jdbcTemplate).query(sqlCaptor.capture(), any(PreparedStatementSetter.class), any(RowMapper.class));
        String sql = sqlCaptor.getValue();
        assertTrue(sql.contains("FROM metric_1h "), "Should query hourly aggregate");
        assertTrue(sql.contains("power_consumption_avg"), "Should use aggregate column");
    }

    @Test
    void power_byUser_yearRange_queriesDailyAggregate() {
        powerStrategy.getAggregatedByUser(USER_ID, NOW.minusDays(180), NOW, BUCKET);

        verify(jdbcTemplate).query(sqlCaptor.capture(), any(PreparedStatementSetter.class), any(RowMapper.class));
        String sql = sqlCaptor.getValue();
        assertTrue(sql.contains("FROM metric_1d m"), "Should query daily aggregate");
        assertTrue(sql.contains("power_consumption_avg"), "Should use aggregate column");
        assertTrue(sql.contains("JOIN device"), "Should join with device table");
    }
}
