package com.cryptoterm.backend.monitoring.service;

import com.cryptoterm.backend.device.domain.Miner;
import com.cryptoterm.backend.device.application.port.out.MinerRepository;
import com.cryptoterm.backend.monitoring.application.port.out.MetricRepository;
import com.cryptoterm.backend.dto.BtcNetworkData;
import com.cryptoterm.backend.dto.MiningProfitResult;
import com.cryptoterm.backend.service.BitcoinBlockRewardService;
import com.cryptoterm.backend.service.BtcNetworkService;
import com.cryptoterm.backend.service.MiningProfitService;
import com.cryptoterm.backend.shared.infrastructure.external.ExchangeRateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MiningProfitService without Mockito.
 * Uses test doubles and stubs.
 */
class MiningProfitServiceTest {

    private MiningProfitService service;
    private TestMinerRepository minerRepository;
    private TestMetricRepository metricRepository;
    private TestBtcNetworkService btcNetworkService;
    private TestBitcoinBlockRewardService bitcoinBlockRewardService;
    private TestExchangeRateService exchangeRateService;

    @BeforeEach
    void setUp() {
        minerRepository = new TestMinerRepository();
        metricRepository = new TestMetricRepository();
        btcNetworkService = new TestBtcNetworkService();
        bitcoinBlockRewardService = new TestBitcoinBlockRewardService();
        exchangeRateService = new TestExchangeRateService();

        service = new MiningProfitService(
                minerRepository,
                metricRepository,
                btcNetworkService,
                bitcoinBlockRewardService,
                exchangeRateService
        );
    }

    @Test
    void testCalculate_NoMiners_ReturnsZero() {
        UUID userId = UUID.randomUUID();
        OffsetDateTime from = OffsetDateTime.now().minusDays(1);
        OffsetDateTime to = OffsetDateTime.now();

        MiningProfitResult result = service.calculate(userId, from, to);

        assertNotNull(result);
        assertEquals(0, result.btcMined());
        assertEquals(0, result.revenueUsd());
        assertEquals(0, result.avgHashrateThs());
    }

    @Test
    void testCalculate_OneMinerWithMetrics_CalculatesCorrectly() {
        UUID userId = UUID.randomUUID();
        UUID minerId = UUID.randomUUID();
        OffsetDateTime from = OffsetDateTime.now().minusDays(1);
        OffsetDateTime to = OffsetDateTime.now();

        // Setup test data
        Miner miner = new Miner();
        miner.setId(minerId);
        minerRepository.addMiner(userId, miner);

        // Miner worked for 1 hour (3600 seconds) with 100 TH/s and 3000W
        metricRepository.setWorkedSeconds(minerId, 3600);
        metricRepository.setAverageHashrate(minerId, 100.0); // 100 TH/s
        metricRepository.setAveragePower(minerId, 3000.0); // 3000 W

        // Execute
        MiningProfitResult result = service.calculate(userId, from, to);

        // Verify
        assertNotNull(result);
        assertEquals(100.0, result.avgHashrateThs(), 0.01);
        assertEquals(3000.0, result.avgPowerConsumptionW(), 0.01);
        assertEquals(1.0, result.workedHours(), 0.01);
        assertTrue(result.btcMined() > 0);
        assertTrue(result.revenueUsd() > 0);
        assertEquals(100000.0, result.btcPriceUsd(), 0.01);
        assertEquals(90.0, result.usdRubRate(), 0.01);
        assertEquals(50e12, result.difficulty(), 0.01);
    }

    @Test
    void testCalculate_MultipleMiners_AggregatesCorrectly() {
        UUID userId = UUID.randomUUID();
        UUID miner1Id = UUID.randomUUID();
        UUID miner2Id = UUID.randomUUID();
        OffsetDateTime from = OffsetDateTime.now().minusDays(1);
        OffsetDateTime to = OffsetDateTime.now();

        // Setup miners
        Miner miner1 = new Miner();
        miner1.setId(miner1Id);
        Miner miner2 = new Miner();
        miner2.setId(miner2Id);
        minerRepository.addMiner(userId, miner1);
        minerRepository.addMiner(userId, miner2);

        // Miner 1: 1 hour, 100 TH/s, 3000W
        metricRepository.setWorkedSeconds(miner1Id, 3600);
        metricRepository.setAverageHashrate(miner1Id, 100.0);
        metricRepository.setAveragePower(miner1Id, 3000.0);

        // Miner 2: 2 hours, 50 TH/s, 1500W
        metricRepository.setWorkedSeconds(miner2Id, 7200);
        metricRepository.setAverageHashrate(miner2Id, 50.0);
        metricRepository.setAveragePower(miner2Id, 1500.0);

        // Execute
        MiningProfitResult result = service.calculate(userId, from, to);

        // Verify weighted averages
        // Hashrate: (100*1 + 50*2) / 3 = 200/3 = 66.67 TH/s
        // Power: (3000*1 + 1500*2) / 3 = 6000/3 = 2000 W
        assertNotNull(result);
        assertEquals(66.67, result.avgHashrateThs(), 0.01);
        assertEquals(2000.0, result.avgPowerConsumptionW(), 0.01);
        assertEquals(3.0, result.workedHours(), 0.01);
        assertTrue(result.btcMined() > 0);
    }

    @Test
    void testCalculate_MinerWithoutPowerMetrics_StillCalculates() {
        UUID userId = UUID.randomUUID();
        UUID minerId = UUID.randomUUID();
        OffsetDateTime from = OffsetDateTime.now().minusDays(1);
        OffsetDateTime to = OffsetDateTime.now();

        Miner miner = new Miner();
        miner.setId(minerId);
        minerRepository.addMiner(userId, miner);

        metricRepository.setWorkedSeconds(minerId, 3600);
        metricRepository.setAverageHashrate(minerId, 100.0);
        metricRepository.setAveragePower(minerId, null); // No power data

        MiningProfitResult result = service.calculate(userId, from, to);

        assertNotNull(result);
        assertEquals(100.0, result.avgHashrateThs(), 0.01);
        assertEquals(0.0, result.avgPowerConsumptionW(), 0.01);
        assertTrue(result.btcMined() > 0);
    }

    @Test
    void testCalculate_MinerWithZeroHashrate_Skipped() {
        UUID userId = UUID.randomUUID();
        UUID minerId = UUID.randomUUID();
        OffsetDateTime from = OffsetDateTime.now().minusDays(1);
        OffsetDateTime to = OffsetDateTime.now();

        Miner miner = new Miner();
        miner.setId(minerId);
        minerRepository.addMiner(userId, miner);

        metricRepository.setWorkedSeconds(minerId, 3600);
        metricRepository.setAverageHashrate(minerId, 0.0); // Zero hashrate

        MiningProfitResult result = service.calculate(userId, from, to);

        assertNotNull(result);
        assertEquals(0.0, result.btcMined());
        assertEquals(0.0, result.revenueUsd());
    }

    // Test doubles

    static class TestMinerRepository implements MinerRepository {
        private final List<Miner> miners = new ArrayList<>();
        private UUID userId;

        void addMiner(UUID userId, Miner miner) {
            this.userId = userId;
            miners.add(miner);
        }

        @Override
        public List<Miner> findAllByUserId(UUID userId) {
            if (this.userId != null && this.userId.equals(userId)) {
                return new ArrayList<>(miners);
            }
            return new ArrayList<>();
        }

        // Other methods not implemented for this test
        @Override public List<Miner> findAll() { return null; }
        @Override public <S extends Miner> S save(S entity) { return null; }
        @Override public java.util.Optional<Miner> findById(UUID id) { return java.util.Optional.empty(); }
        @Override public boolean existsById(UUID id) { return false; }
        @Override public List<Miner> findAllById(Iterable<UUID> ids) { return null; }
        @Override public long count() { return 0; }
        @Override public void deleteById(UUID id) {}
        @Override public void delete(Miner entity) {}
        @Override public void deleteAllById(Iterable<? extends UUID> ids) {}
        @Override public void deleteAll(Iterable<? extends Miner> entities) {}
        @Override public void deleteAll() {}
        @Override public List<Miner> findByDevice_Id(UUID deviceId) { return null; }
        @Override public List<Miner> findByDeviceIdIn(List<UUID> deviceIds) { return null; }
        @Override public void flush() {}
        @Override public <S extends Miner> S saveAndFlush(S entity) { return null; }
        @Override public <S extends Miner> List<S> saveAllAndFlush(Iterable<S> entities) { return null; }
        @Override public void deleteAllInBatch(Iterable<Miner> entities) {}
        @Override public void deleteAllByIdInBatch(Iterable<UUID> ids) {}
        @Override public void deleteAllInBatch() {}
        @Override @Deprecated public Miner getOne(UUID id) { return null; }
        @Override @Deprecated public Miner getById(UUID id) { return null; }
        @Override public Miner getReferenceById(UUID id) { return null; }
        @Override public <S extends Miner> List<S> findAll(Example<S> example) { return null; }
        @Override public <S extends Miner> List<S> findAll(Example<S> example, Sort sort) { return null; }
        @Override public <S extends Miner> List<S> saveAll(Iterable<S> entities) { return null; }
        @Override public List<Miner> findAll(Sort sort) { return null; }
        @Override public Page<Miner> findAll(Pageable pageable) { return null; }
        @Override public <S extends Miner> java.util.Optional<S> findOne(Example<S> example) { return null; }
        @Override public <S extends Miner> Page<S> findAll(Example<S> example, Pageable pageable) { return null; }
        @Override public <S extends Miner> long count(Example<S> example) { return 0; }
        @Override public <S extends Miner> boolean exists(Example<S> example) { return false; }
        @Override public <S extends Miner, R> R findBy(Example<S> example, java.util.function.Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { return null; }
    }

    static class TestMetricRepository implements MetricRepository {
        private final java.util.Map<UUID, Long> workedSeconds = new java.util.HashMap<>();
        private final java.util.Map<UUID, Double> avgHashrates = new java.util.HashMap<>();
        private final java.util.Map<UUID, Double> avgPowers = new java.util.HashMap<>();

        void setWorkedSeconds(UUID minerId, long seconds) {
            workedSeconds.put(minerId, seconds);
        }

        void setAverageHashrate(UUID minerId, Double hashrate) {
            avgHashrates.put(minerId, hashrate);
        }

        void setAveragePower(UUID minerId, Double power) {
            avgPowers.put(minerId, power);
        }

        @Override
        public long calculateWorkedSeconds(UUID minerId, OffsetDateTime from, OffsetDateTime to) {
            return workedSeconds.getOrDefault(minerId, 0L);
        }

        @Override
        public Double averageWorkingHashrate(UUID minerId, OffsetDateTime from, OffsetDateTime to) {
            return avgHashrates.get(minerId);
        }

        @Override
        public Double averagePowerConsumption(UUID minerId, OffsetDateTime from, OffsetDateTime to) {
            return avgPowers.get(minerId);
        }

        // Other methods not needed
        @Override public java.util.List<com.cryptoterm.backend.monitoring.domain.Metric> findAll() { return null; }
        @Override public <S extends com.cryptoterm.backend.monitoring.domain.Metric> S save(S entity) { return null; }
        @Override public java.util.Optional<com.cryptoterm.backend.monitoring.domain.Metric> findById(OffsetDateTime id) { return null; }
        @Override public boolean existsById(OffsetDateTime id) { return false; }
        @Override public List<com.cryptoterm.backend.monitoring.domain.Metric> findAllById(Iterable<OffsetDateTime> ids) { return null; }
        @Override public long count() { return 0; }
        @Override public void deleteById(OffsetDateTime id) {}
        @Override public void delete(com.cryptoterm.backend.monitoring.domain.Metric entity) {}
        @Override public void deleteAllById(Iterable<? extends OffsetDateTime> ids) {}
        @Override public void deleteAll(Iterable<? extends com.cryptoterm.backend.monitoring.domain.Metric> entities) {}
        @Override public void deleteAll() {}
        @Override public java.util.List<com.cryptoterm.backend.monitoring.domain.Metric> findByDevice_IdAndTimeBetween(
                UUID deviceId, OffsetDateTime from, OffsetDateTime to) { return null; }
        @Override public java.util.List<com.cryptoterm.backend.monitoring.domain.Metric> findByMiner_IdAndTimeBetween(
                UUID minerId, OffsetDateTime from, OffsetDateTime to) { return null; }
        @Override public void flush() {}
        @Override public <S extends com.cryptoterm.backend.monitoring.domain.Metric> S saveAndFlush(S entity) { return null; }
        @Override public <S extends com.cryptoterm.backend.monitoring.domain.Metric> List<S> saveAllAndFlush(Iterable<S> entities) { return null; }
        @Override public void deleteAllInBatch(Iterable<com.cryptoterm.backend.monitoring.domain.Metric> entities) {}
        @Override public void deleteAllByIdInBatch(Iterable<OffsetDateTime> ids) {}
        @Override public void deleteAllInBatch() {}
        @Override @Deprecated public com.cryptoterm.backend.monitoring.domain.Metric getOne(OffsetDateTime id) { return null; }
        @Override @Deprecated public com.cryptoterm.backend.monitoring.domain.Metric getById(OffsetDateTime id) { return null; }
        @Override public com.cryptoterm.backend.monitoring.domain.Metric getReferenceById(OffsetDateTime id) { return null; }
        @Override public <S extends com.cryptoterm.backend.monitoring.domain.Metric> List<S> findAll(Example<S> example) { return null; }
        @Override public <S extends com.cryptoterm.backend.monitoring.domain.Metric> List<S> findAll(Example<S> example, Sort sort) { return null; }
        @Override public <S extends com.cryptoterm.backend.monitoring.domain.Metric> List<S> saveAll(Iterable<S> entities) { return null; }
        @Override public List<com.cryptoterm.backend.monitoring.domain.Metric> findAll(Sort sort) { return null; }
        @Override public Page<com.cryptoterm.backend.monitoring.domain.Metric> findAll(Pageable pageable) { return null; }
        @Override public <S extends com.cryptoterm.backend.monitoring.domain.Metric> java.util.Optional<S> findOne(Example<S> example) { return null; }
        @Override public <S extends com.cryptoterm.backend.monitoring.domain.Metric> Page<S> findAll(Example<S> example, Pageable pageable) { return null; }
        @Override public <S extends com.cryptoterm.backend.monitoring.domain.Metric> long count(Example<S> example) { return 0; }
        @Override public <S extends com.cryptoterm.backend.monitoring.domain.Metric> boolean exists(Example<S> example) { return false; }
        @Override public <S extends com.cryptoterm.backend.monitoring.domain.Metric, R> R findBy(Example<S> example, java.util.function.Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { return null; }
    }

    static class TestBtcNetworkService extends BtcNetworkService {
        public TestBtcNetworkService() {
            super(org.springframework.web.reactive.function.client.WebClient.builder());
        }

        @Override
        public BtcNetworkData loadNetworkData() {
            // Return test data: $100k BTC, 50T difficulty
            return new BtcNetworkData(100000.0, 50e12);
        }
    }

    static class TestBitcoinBlockRewardService extends BitcoinBlockRewardService {
        @Override
        public double getCurrentBlockReward() {
            return 3.125; // Current block reward after 2024 halving
        }
    }

    static class TestExchangeRateService extends ExchangeRateService {
        public TestExchangeRateService() {
            super(org.springframework.web.reactive.function.client.WebClient.builder());
        }

        @Override
        public double getUsdRubRate() {
            return 90.0; // Test rate
        }
    }
}
