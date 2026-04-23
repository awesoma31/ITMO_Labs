package com.cryptoterm.backend.service.strategy;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Стратегия для получения агрегированных метрик.
 * Позволяет обрабатывать различные типы метрик (температура, хэшрейт) единообразно.
 */
public interface MetricStrategy {
    
    /**
     * Точка данных метрики с временной меткой и значением.
     */
    record AggregatedMetricPoint(OffsetDateTime time, BigDecimal value) {}
    
    /**
     * Получить агрегированные метрики для конкретного устройства.
     *
     * @param deviceId ID устройства
     * @param from начальная дата
     * @param to конечная дата
     * @param bucket интервал агрегации (например, "1 minute", "5 minutes", "1 hour")
     * @return список точек метрики
     */
    List<AggregatedMetricPoint> getAggregatedByDevice(UUID deviceId, OffsetDateTime from, OffsetDateTime to, String bucket);
    
    /**
     * Получить агрегированные метрики для всех устройств пользователя.
     *
     * @param userId ID пользователя
     * @param from начальная дата
     * @param to конечная дата
     * @param bucket интервал агрегации (например, "1 minute", "5 minutes", "1 hour")
     * @return список точек метрики
     */
    List<AggregatedMetricPoint> getAggregatedByUser(UUID userId, OffsetDateTime from, OffsetDateTime to, String bucket);
    
    /**
     * Получить название метрики.
     *
     * @return название метрики
     */
    String getMetricName();
}
