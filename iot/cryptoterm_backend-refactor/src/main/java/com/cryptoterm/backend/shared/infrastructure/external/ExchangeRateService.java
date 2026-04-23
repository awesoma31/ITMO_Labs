package com.cryptoterm.backend.shared.infrastructure.external;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Service for fetching USD/RUB exchange rate.
 */
@Service
public class ExchangeRateService {
    private static final Logger log = LoggerFactory.getLogger(ExchangeRateService.class);

    private final WebClient webClient;

    public ExchangeRateService(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    /**
     * Get current USD to RUB exchange rate.
     * 
     * @return USD/RUB rate
     */
    public double getUsdRubRate() {
        List<Supplier<Double>> providers = List.of(
                this::fromExchangeRateApi,
                this::fromCurrencyApi
        );

        for (Supplier<Double> provider : providers) {
            try {
                return provider.get();
            } catch (Exception e) {
                log.warn("USD/RUB rate provider failed: {}", e.getMessage());
            }
        }

        // Fallback to approximate rate if all APIs fail
        log.warn("All USD/RUB providers failed, using fallback rate");
        return 90.0; // Approximate fallback rate
    }

    /**
     * SOURCE 1: exchangerate-api.com
     */
    private double fromExchangeRateApi() {
        Map<?, ?> response = webClient.get()
                .uri("https://api.exchangerate-api.com/v4/latest/USD")
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        Map<?, ?> rates = (Map<?, ?>) response.get("rates");
        return ((Number) rates.get("RUB")).doubleValue();
    }

    /**
     * SOURCE 2: currencyapi.com (alternative)
     */
    private double fromCurrencyApi() {
        Map<?, ?> response = webClient.get()
                .uri("https://api.currencyapi.com/v3/latest?apikey=YOUR_API_KEY&currencies=RUB&base_currency=USD")
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        Map<?, ?> data = (Map<?, ?>) response.get("data");
        Map<?, ?> rub = (Map<?, ?>) data.get("RUB");
        return ((Number) rub.get("value")).doubleValue();
    }
}
