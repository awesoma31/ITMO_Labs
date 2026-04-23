package com.cryptoterm.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Service
public class BitcoinDifficultyService {

    private final RestTemplate restTemplate = new RestTemplate();

    public BigDecimal getDifficulty() {
        List<Supplier<BigDecimal>> sources = List.of(
                this::fromBlockchainInfo,
                this::fromBlockchair
        );

        for (Supplier<BigDecimal> source : sources) {
            try {
                return source.get();
            } catch (Exception ignored) {}
        }
        throw new IllegalStateException("BTC difficulty sources unavailable");
    }

    private BigDecimal fromBlockchainInfo() {
        return new BigDecimal(
                restTemplate.getForObject(
                        "https://blockchain.info/q/getdifficulty",
                        String.class
                )
        );
    }

    private BigDecimal fromBlockchair() {
        var response = restTemplate.getForObject(
                "https://api.blockchair.com/bitcoin/stats",
                Map.class
        );
        return new BigDecimal(
                ((Map<?, ?>) response.get("data")).get("difficulty").toString()
        );
    }
}
