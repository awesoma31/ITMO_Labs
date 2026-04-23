package com.cryptoterm.backend.command.domain.asic;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * Represents a single step in an ASIC command workflow.
 * Shared between templates and executable commands.
 */
public class AsicCommandStep {
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("request")
    private AsicHttpRequest request;
    
    @JsonProperty("extract")
    private Map<String, String> extract;

    public AsicCommandStep() {}

    public AsicCommandStep(String id, AsicHttpRequest request, Map<String, String> extract) {
        this.id = id;
        this.request = request;
        this.extract = extract;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public AsicHttpRequest getRequest() { return request; }
    public void setRequest(AsicHttpRequest request) { this.request = request; }

    public Map<String, String> getExtract() { return extract; }
    public void setExtract(Map<String, String> extract) { this.extract = extract; }
}
