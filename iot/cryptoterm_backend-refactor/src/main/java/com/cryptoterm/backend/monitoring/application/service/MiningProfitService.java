package com.cryptoterm.backend.service;

import com.cryptoterm.backend.device.domain.Device;
import com.cryptoterm.backend.device.domain.Miner;
import com.cryptoterm.backend.dto.BtcNetworkData;
import com.cryptoterm.backend.dto.MiningProfitResult;
import com.cryptoterm.backend.device.application.port.out.DeviceRepository;
import com.cryptoterm.backend.monitoring.application.port.out.MetricRepository;
import com.cryptoterm.backend.device.application.port.out.MinerRepository;
import com.cryptoterm.backend.shared.infrastructure.external.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class MiningProfitService {
    private static final Logger log = LoggerFactory.getLogger(MiningProfitService.class);
    private static final double TWO_POW_32 = 4294967296.0;

    private final MinerRepository minerRepository;
    private final MetricRepository metricRepository;
    private final BtcNetworkService btcNetworkService;
    private final BitcoinBlockRewardService bitcoinBlockRewardService;
    private final ExchangeRateService exchangeRateService;

    public MiningProfitService(
            MinerRepository minerRepository,
            MetricRepository metricRepository,
            BtcNetworkService btcNetworkService,
            BitcoinBlockRewardService bitcoinBlockRewardService,
            ExchangeRateService exchangeRateService

    ) {
        this.minerRepository = minerRepository;
        this.metricRepository = metricRepository;
        this.btcNetworkService = btcNetworkService;
        this.bitcoinBlockRewardService = bitcoinBlockRewardService;
        this.exchangeRateService = exchangeRateService;
    }

    public MiningProfitResult calculate(
            UUID userId,
            OffsetDateTime from,
            OffsetDateTime to
    ) {
        OffsetDateTime end = to != null ? to : OffsetDateTime.now();

        List<Miner> miners = minerRepository.findAllByUserId(userId);

        double totalWorkedHours = 0.0;
        double weightedHashrateSum = 0.0;
        double weightedPowerSum = 0.0;

        for (Miner miner : miners) {

            long workedSeconds = metricRepository.calculateWorkedSeconds(
                    miner.getId(), from, end
            );

            if (workedSeconds <= 0) {
                continue;
            }

            double workedHours = workedSeconds / 3600.0;
            
            // Average hashrate
            Double avgHashrateThs = metricRepository.averageWorkingHashrate(
                    miner.getId(), from, end
            );

            if (avgHashrateThs == null || avgHashrateThs <= 0) {
                continue;
            }

            // Average power consumption
            Double avgPowerW = metricRepository.averagePowerConsumption(
                    miner.getId(), from, end
            );
            
            if (avgPowerW != null && avgPowerW > 0) {
                weightedPowerSum += avgPowerW * workedHours;
            }

            totalWorkedHours += workedHours;
            weightedHashrateSum += avgHashrateThs * workedHours;
        }

        if (totalWorkedHours <= 0) {
            return MiningProfitResult.zero(userId, from, end);
        }
        
        double avgHashrateThs = weightedHashrateSum / totalWorkedHours;
        double avgPowerConsumptionW = weightedPowerSum / totalWorkedHours;
        
        // Get network data
        BtcNetworkData network = btcNetworkService.loadNetworkData();
        
        // Get USD/RUB rate
        double usdRubRate = exchangeRateService.getUsdRubRate();
        
        // Calculate mining profit
        double hashrateHs = avgHashrateThs * 1e9; // TH/s to H/s
        double blockReward = bitcoinBlockRewardService.getCurrentBlockReward();
        double btcPerSecond =
                (hashrateHs / (network.difficulty() * TWO_POW_32))
                        * blockReward;
        double totalWorkedSeconds = totalWorkedHours * 3600.0;
        double btcMined = btcPerSecond * totalWorkedSeconds;
        double revenueUsd = btcMined * network.btcPriceUsd();

        log.info("Calculated profit for user {}: {} TH/s, {} W, {} BTC, {} USD", 
                 userId, avgHashrateThs, avgPowerConsumptionW, btcMined, revenueUsd);

        return new MiningProfitResult(
                userId,
                from,
                end,
                avgHashrateThs,
                avgPowerConsumptionW,
                totalWorkedHours,
                btcMined,
                revenueUsd,
                network.btcPriceUsd(),
                usdRubRate,
                network.difficulty()
        );
    }
}
