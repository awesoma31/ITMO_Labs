package com.cryptoterm.backend.monitoring.strategy;

import com.cryptoterm.backend.service.strategy.MetricSourceResolver;
import com.cryptoterm.backend.service.strategy.MetricSourceResolver.Source;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class MetricSourceResolverTest {

    private static final OffsetDateTime NOW = OffsetDateTime.now(ZoneOffset.UTC);

    @Test
    void resolve_withinThreeDays_returnsRawTable() {
        Source src = MetricSourceResolver.resolve(NOW.minusHours(6), NOW);

        assertEquals("metric", src.table());
        assertEquals("time", src.timeColumn());
        assertEquals("temperature_c", src.temperatureColumn());
        assertEquals("hash_rate_ths", src.hashRateColumn());
        assertEquals("power_consumption_w", src.powerColumn());
    }

    @Test
    void resolve_exactlyThreeDays_returnsRawTable() {
        Source src = MetricSourceResolver.resolve(NOW.minusDays(3), NOW);

        assertEquals("metric", src.table());
    }

    @Test
    void resolve_fourDays_returnsHourlyAggregate() {
        Source src = MetricSourceResolver.resolve(NOW.minusDays(4), NOW);

        assertEquals("metric_1h", src.table());
        assertEquals("bucket", src.timeColumn());
        assertEquals("temperature_avg", src.temperatureColumn());
        assertEquals("hash_rate_avg", src.hashRateColumn());
        assertEquals("power_consumption_avg", src.powerColumn());
    }

    @Test
    void resolve_thirtyDays_returnsHourlyAggregate() {
        Source src = MetricSourceResolver.resolve(NOW.minusDays(30), NOW);

        assertEquals("metric_1h", src.table());
    }

    @Test
    void resolve_thirtyOneDays_returnsDailyAggregate() {
        Source src = MetricSourceResolver.resolve(NOW.minusDays(31), NOW);

        assertEquals("metric_1d", src.table());
        assertEquals("bucket", src.timeColumn());
        assertEquals("temperature_avg", src.temperatureColumn());
        assertEquals("hash_rate_avg", src.hashRateColumn());
        assertEquals("power_consumption_avg", src.powerColumn());
    }

    @Test
    void resolve_oneYear_returnsDailyAggregate() {
        Source src = MetricSourceResolver.resolve(NOW.minusDays(365), NOW);

        assertEquals("metric_1d", src.table());
    }

    @ParameterizedTest
    @CsvSource({
        "1,   metric",
        "2,   metric",
        "3,   metric",
        "7,   metric_1h",
        "14,  metric_1h",
        "30,  metric_1h",
        "60,  metric_1d",
        "90,  metric_1d",
        "365, metric_1d"
    })
    void resolve_variousRanges_returnsExpectedTable(int daysBack, String expectedTable) {
        Source src = MetricSourceResolver.resolve(NOW.minusDays(daysBack), NOW);

        assertEquals(expectedTable, src.table());
    }
}
