package com.cryptoterm.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Service
public class BitcoinPriceService {

    private final RestTemplate restTemplate = new RestTemplate();

    public BigDecimal getBtcUsdPrice() {
        List<Supplier<BigDecimal>> sources = List.of(
                this::fromCoinGecko,
                this::fromCoinDesk
        );

        for (Supplier<BigDecimal> source : sources) {
            try {
                return source.get();
            } catch (Exception ignored) {}
        }
        throw new IllegalStateException("BTC price sources unavailable");
    }

    private BigDecimal fromCoinGecko() {
        var response = restTemplate.getForObject(
                "https://api.coingecko.com/api/v3/simple/price?ids=bitcoin&vs_currencies=usd",
                Map.class
        );
        return new BigDecimal(
                ((Map<?, ?>) response.get("bitcoin")).get("usd").toString()
        );
    }

    private BigDecimal fromCoinDesk() {
        var response = restTemplate.getForObject(
                "https://api.coindesk.com/v1/bpi/currentprice/USD.json",
                Map.class
        );
        return new BigDecimal(
                ((Map<?, ?>) ((Map<?, ?>) response.get("bpi")).get("USD")).get("rate_float").toString()
        );
    }
}
