package com.cryptoterm.backend.service.strategy;

import java.time.Duration;
import java.time.OffsetDateTime;

/**
 * Determines the optimal data source table based on the requested time range.
 *
 * <ul>
 *   <li>≤ 3 days  → raw {@code metric} table (full resolution)</li>
 *   <li>≤ 30 days → {@code metric_1h} continuous aggregate</li>
 *   <li>> 30 days → {@code metric_1d} continuous aggregate</li>
 * </ul>
 */
public final class MetricSourceResolver {

    private MetricSourceResolver() {}

    public record Source(
            String table,
            String timeColumn,
            String temperatureColumn,
            String hashRateColumn,
            String powerColumn
    ) {}

    private static final Source RAW = new Source(
            "metric", "time",
            "temperature_c", "hash_rate_ths", "power_consumption_w"
    );

    private static final Source HOURLY = new Source(
            "metric_1h", "bucket",
            "temperature_avg", "hash_rate_avg", "power_consumption_avg"
    );

    private static final Source DAILY = new Source(
            "metric_1d", "bucket",
            "temperature_avg", "hash_rate_avg", "power_consumption_avg"
    );

    public static Source resolve(OffsetDateTime from, OffsetDateTime to) {
        long days = Duration.between(from, to).toDays();
        if (days <= 3) return RAW;
        if (days <= 30) return HOURLY;
        return DAILY;
    }
}
