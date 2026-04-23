package com.cryptoterm.backend.service;

import com.cryptoterm.backend.device.domain.Device;
import com.cryptoterm.backend.device.domain.Miner;
import com.cryptoterm.backend.device.application.port.out.DeviceRepository;
import com.cryptoterm.backend.device.application.port.out.MinerRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MinerService {
    private final MinerRepository minerRepository;
    public MinerService(MinerRepository minerRepository) {
        this.minerRepository = minerRepository;
    }

    public String registerMiner(Device device, String minerName, String vendor, String model) {
        Miner miner = new Miner();
        if (device != null) {
            UUID minerId = UUID.randomUUID();
            miner.setId(minerId);
            miner.setDevice(device);
            miner.setLabel(minerName);
            miner.setVendor(vendor);
            miner.setModel(model);
            minerRepository.save(miner);
            return minerId.toString();
        }
        throw new RuntimeException("Device not found");
    }
}
