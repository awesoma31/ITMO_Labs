package com.cryptoterm.backend.service;

import com.cryptoterm.backend.device.domain.MinerConfig;
import com.cryptoterm.backend.device.application.port.out.MinerConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MinerConfigService {
    @Autowired
    MinerConfigRepository minerConfigRepository;

    public MinerConfig getMinerConfig(String vendor, String model, String mode) {
        return minerConfigRepository.findByVendorAndModelAndMode(vendor, model, mode);
    }

    public List<MinerConfig> getAllMinerConfigs() {
        return minerConfigRepository.findAll();
    }

    public MinerConfig save(MinerConfig config) {
        return minerConfigRepository.save(config);
    }
}
