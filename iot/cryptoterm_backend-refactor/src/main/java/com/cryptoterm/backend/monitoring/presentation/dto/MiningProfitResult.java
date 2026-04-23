package com.cryptoterm.backend.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record MiningProfitResult(
        @JsonProperty("user_id")
        UUID userId,
        OffsetDateTime from,
        OffsetDateTime to,
        @JsonProperty("avg_hashrate_ths")
        double avgHashrateThs,
        @JsonProperty("avg_power_consumption_w")
        double avgPowerConsumptionW,
        @JsonProperty("worked_hours")
        double workedHours,
        @JsonProperty("btc_mined")
        double btcMined,
        @JsonProperty("revenue_usd")
        double revenueUsd,
        @JsonProperty("btc_price_usd")
        double btcPriceUsd,
        @JsonProperty("usd_rub_rate")
        double usdRubRate,
        @JsonProperty("difficulty")
        double difficulty
) {
    public static MiningProfitResult zero(
            UUID userId,
            OffsetDateTime from,
            OffsetDateTime to
    ) {
        return new MiningProfitResult(
                userId, from, to,
                0, 0, 0, 0, 0, 0, 0, 0
        );
    }
}