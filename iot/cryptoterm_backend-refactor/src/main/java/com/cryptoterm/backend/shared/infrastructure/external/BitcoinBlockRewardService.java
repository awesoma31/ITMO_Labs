package com.cryptoterm.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BitcoinBlockRewardService {

    private final RestTemplate restTemplate = new RestTemplate();

    public double getCurrentBlockReward() {
        long height = getCurrentBlockHeight();
        return calculateBlockReward(height);
    }
    private long getCurrentBlockHeight() {
        String response = restTemplate.getForObject(
                "https://blockchain.info/q/getblockcount",
                String.class
        );

        if (response == null) {
            throw new IllegalStateException("Failed to load BTC block height");
        }

        return Long.parseLong(response.trim());
    }

    private double calculateBlockReward(long blockHeight) {
        int halvings = (int) (blockHeight / 210_000);

        if (halvings >= 64) {
            return 0.0;
        }

        return 50.0 / Math.pow(2, halvings);
    }
}
