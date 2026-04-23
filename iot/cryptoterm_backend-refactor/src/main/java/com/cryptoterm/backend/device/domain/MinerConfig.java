package com.cryptoterm.backend.device.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "miner_configs")
public class MinerConfig {

    @Id
    private String id;

    @Field("type")
    private String vendor;

    @Field("model")
    private String model;

    @Field("mode")
    private String mode;

    @Field("json_config")
    private String jsonConfig;

    public MinerConfig() {}

    public MinerConfig(String vendor, String model, String mode, String jsonConfig) {
        this.vendor = vendor;
        this.model = model;
        this.mode = mode;
        this.jsonConfig = jsonConfig;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getVendor() { return vendor; }
    public void setVendor(String vendor) { this.vendor = vendor; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public String getJsonConfig() { return jsonConfig; }
    public void setJsonConfig(String jsonConfig) { this.jsonConfig = jsonConfig; }
}
