package com.cryptoterm.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BtcNetworkData(
        @JsonProperty("btc_price_usd")
        double btcPriceUsd,
        double difficulty
) {}
