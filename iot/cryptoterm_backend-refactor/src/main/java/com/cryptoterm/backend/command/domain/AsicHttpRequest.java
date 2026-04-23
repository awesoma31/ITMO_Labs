package com.cryptoterm.backend.command.domain.asic;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * Represents an HTTP request to an ASIC miner.
 * Shared between templates and executable commands.
 */
public class AsicHttpRequest {
    @JsonProperty("method")
    private String method;
    
    @JsonProperty("path")
    private String path;
    
    @JsonProperty("headers")
    private Map<String, String> headers;
    
    @JsonProperty("body")
    private Object body;
    
    @JsonProperty("timeoutMs")
    private Integer timeoutMs;

    public AsicHttpRequest() {}

    public AsicHttpRequest(String method, String path, Map<String, String> headers, 
                          Object body, Integer timeoutMs) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body;
        this.timeoutMs = timeoutMs;
    }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public Map<String, String> getHeaders() { return headers; }
    public void setHeaders(Map<String, String> headers) { this.headers = headers; }

    public Object getBody() { return body; }
    public void setBody(Object body) { this.body = body; }

    public Integer getTimeoutMs() { return timeoutMs; }
    public void setTimeoutMs(Integer timeoutMs) { this.timeoutMs = timeoutMs; }
}
