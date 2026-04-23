package com.cryptoterm.backend.service;

import com.cryptoterm.backend.dto.BtcNetworkData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Service
public class BtcNetworkService {
    private static final Logger log = LoggerFactory.getLogger(BtcNetworkService.class);

    private final WebClient webClient;

    public BtcNetworkService(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    public BtcNetworkData loadNetworkData() {

        List<Supplier<BtcNetworkData>> providers = List.of(
                this::fromBlockchainInfo,
                this::fromBlockchair
        );

        for (Supplier<BtcNetworkData> provider : providers) {
            try {
                return provider.get();
            } catch (Exception e) {
                log.warn("BTC network provider failed: {}", e.getMessage());
            }
        }

        throw new IllegalStateException("BTC network data unavailable from all providers");
    }

    /**
     * SOURCE 1:
     * https://blockchain.info
     */
    private BtcNetworkData fromBlockchainInfo() {

        // price
        Map<?, ?> ticker = webClient.get()
                .uri("https://api.blockchain.info/ticker")
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        Map<?, ?> usd = (Map<?, ?>) ticker.get("USD");
        double priceUsd = ((Number) usd.get("last")).doubleValue();

        // difficulty
        String difficultyRaw = webClient.get()
                .uri("https://blockchain.info/q/getdifficulty")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        double difficulty = Double.parseDouble(difficultyRaw);

        return new BtcNetworkData(priceUsd, difficulty);
    }

    /**
     * SOURCE 2:
     * https://blockchair.com
     */
    private BtcNetworkData fromBlockchair() {

        Map<?, ?> response = webClient.get()
                .uri("https://api.blockchair.com/bitcoin/stats")
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        Map<?, ?> data = (Map<?, ?>) response.get("data");

        double priceUsd = ((Number) data.get("market_price_usd")).doubleValue();
        double difficulty = ((Number) data.get("difficulty")).doubleValue();

        return new BtcNetworkData(priceUsd, difficulty);
    }
}
