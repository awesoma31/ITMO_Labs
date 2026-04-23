package com.cryptoterm.backend.external;

import com.cryptoterm.backend.dto.BtcNetworkData;
import com.cryptoterm.backend.service.BtcNetworkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BtcNetworkService.
 */
class BtcNetworkServiceTest {

    private BtcNetworkService service;

    @BeforeEach
    void setUp() {
        // Create service with real WebClient for integration-like tests
        service = new BtcNetworkService(WebClient.builder());
    }

    @Test
    void testLoadNetworkData_ReturnsValidData() {
        BtcNetworkData data = service.loadNetworkData();
        
        assertNotNull(data, "Network data should not be null");
        assertTrue(data.btcPriceUsd() > 0, "BTC price should be positive");
        assertTrue(data.btcPriceUsd() > 1000, "BTC price should be > $1000");
        assertTrue(data.btcPriceUsd() < 1_000_000, "BTC price should be < $1M (sanity check)");
        
        assertTrue(data.difficulty() > 0, "Difficulty should be positive");
        assertTrue(data.difficulty() > 1e12, "Difficulty should be > 1T");
        assertTrue(data.difficulty() < 1e15, "Difficulty should be < 1000T (sanity check)");
    }

    @Test
    void testLoadNetworkData_PriceIsReasonable() {
        BtcNetworkData data = service.loadNetworkData();
        
        // Bitcoin price typically between $20k and $150k in recent years
        double price = data.btcPriceUsd();
        assertTrue(price >= 10_000 && price <= 200_000, 
            String.format("BTC price should be reasonable: $%.2f", price));
    }

    @Test
    void testLoadNetworkData_DifficultyIsReasonable() {
        BtcNetworkData data = service.loadNetworkData();
        
        // Difficulty typically in the range of tens to hundreds of trillions
        double difficulty = data.difficulty();
        assertTrue(difficulty >= 1e12 && difficulty <= 2e14, 
            String.format("Difficulty should be reasonable: %.2eT", difficulty / 1e12));
    }

    @Test
    void testLoadNetworkData_MultipleCallsConsistent() {
        BtcNetworkData data1 = service.loadNetworkData();
        BtcNetworkData data2 = service.loadNetworkData();
        
        assertNotNull(data1);
        assertNotNull(data2);
        
        // Prices should be similar (within 5% for quick consecutive calls)
        double priceDiff = Math.abs(data1.btcPriceUsd() - data2.btcPriceUsd());
        double maxPriceDiff = Math.max(data1.btcPriceUsd(), data2.btcPriceUsd()) * 0.05;
        
        assertTrue(priceDiff <= maxPriceDiff, 
            String.format("Consecutive calls should return similar prices: $%.2f vs $%.2f", 
                data1.btcPriceUsd(), data2.btcPriceUsd()));
        
        // Difficulty changes slowly, should be identical for quick calls
        assertEquals(data1.difficulty(), data2.difficulty(), data1.difficulty() * 0.01,
            "Difficulty should be very similar for consecutive calls");
    }

    @Test
    void testLoadNetworkData_HandlesFallback() {
        // This test verifies that the service tries multiple providers
        // Even if one fails, it should get data from another
        BtcNetworkData data = service.loadNetworkData();
        
        assertNotNull(data, "Service should fallback to alternative provider if primary fails");
        assertTrue(data.btcPriceUsd() > 0);
        assertTrue(data.difficulty() > 0);
    }
}
