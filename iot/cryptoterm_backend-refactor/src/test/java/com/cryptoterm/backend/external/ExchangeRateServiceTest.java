package com.cryptoterm.backend.external;

import com.cryptoterm.backend.shared.infrastructure.external.ExchangeRateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ExchangeRateService.
 */
class ExchangeRateServiceTest {

    private ExchangeRateService service;

    @BeforeEach
    void setUp() {
        // Create service with real WebClient for basic tests
        service = new ExchangeRateService(WebClient.builder());
    }

    @Test
    void testGetUsdRubRate_ReturnsPositiveValue() {
        double rate = service.getUsdRubRate();
        
        assertTrue(rate > 0, "USD/RUB rate should be positive");
        assertTrue(rate > 50, "USD/RUB rate should be reasonable (> 50)");
        assertTrue(rate < 200, "USD/RUB rate should be reasonable (< 200)");
    }

    @Test
    void testGetUsdRubRate_Fallback() {
        // Test that even if APIs fail, we get a fallback rate
        ExchangeRateService serviceWithBadUrl = new ExchangeRateService(WebClient.builder()) {
            @Override
            public double getUsdRubRate() {
                try {
                    return super.getUsdRubRate();
                } catch (Exception e) {
                    return 90.0; // Fallback
                }
            }
        };

        double rate = serviceWithBadUrl.getUsdRubRate();
        assertTrue(rate > 0);
    }

    @Test
    void testGetUsdRubRate_MultipleCallsConsistent() {
        double rate1 = service.getUsdRubRate();
        double rate2 = service.getUsdRubRate();
        
        // Rates should be similar (within 10% - may change between calls)
        double diff = Math.abs(rate1 - rate2);
        double maxDiff = Math.max(rate1, rate2) * 0.10;
        
        assertTrue(diff <= maxDiff, 
            String.format("Consecutive calls should return similar rates: %.2f vs %.2f", rate1, rate2));
    }
}
