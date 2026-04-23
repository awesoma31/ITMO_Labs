package com.cryptoterm.backend.dto;

import com.cryptoterm.backend.command.domain.AsicCommandTemplate;
import com.cryptoterm.backend.command.domain.asic.AsicCommandStep;
import com.cryptoterm.backend.command.domain.asic.AsicHttpRequest;
import com.cryptoterm.backend.command.domain.asic.AsicRetryPolicy;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DTO для создания нового шаблона команды ASIC.
 */
public class CreateAsicCommandTemplateRequest {

    @NotBlank(message = "Template name is required (e.g., '1780_watts')")
    private String name;

    private String description;

    @NotBlank(message = "Miner model is required (e.g., 'Antminer S19 Pro Hydro')")
    private String minerModel;

    @NotBlank(message = "Miner vendor is required (e.g., 'Bitmain')")
    private String minerVendor;

    private String firmware; // например, "anthill", "stock", "vnish"

    @NotEmpty(message = "At least one step is required")
    @Valid
    private List<CommandStepDto> steps;

    @Valid
    private RetryPolicyDto policy;

    // Геттеры и сеттеры
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getMinerModel() { return minerModel; }
    public void setMinerModel(String minerModel) { this.minerModel = minerModel; }

    public String getMinerVendor() { return minerVendor; }
    public void setMinerVendor(String minerVendor) { this.minerVendor = minerVendor; }

    public String getFirmware() { return firmware; }
    public void setFirmware(String firmware) { this.firmware = firmware; }

    public List<CommandStepDto> getSteps() { return steps; }
    public void setSteps(List<CommandStepDto> steps) { this.steps = steps; }

    public RetryPolicyDto getPolicy() { return policy; }
    public void setPolicy(RetryPolicyDto policy) { this.policy = policy; }

    /**
     * Преобразует DTO в доменную сущность
     */
    public AsicCommandTemplate toEntity() {
        AsicCommandTemplate template = new AsicCommandTemplate();
        template.setName(this.name);
        template.setDescription(this.description);
        template.setMinerModel(this.minerModel);
        template.setMinerVendor(this.minerVendor);
        template.setFirmware(this.firmware);
        
        // Преобразование шагов
        List<AsicCommandStep> steps = this.steps.stream()
            .map(CommandStepDto::toTemplateStep)
            .collect(Collectors.toList());
        template.setSteps(steps);
        
        // Преобразование политики повторов
        if (this.policy != null) {
            template.setPolicy(this.policy.toTemplatePolicy());
        } else {
            // Политика по умолчанию
            template.setPolicy(new AsicRetryPolicy(2, 2000));
        }
        
        return template;
    }

    // Вложенные DTO
    public static class CommandStepDto {
        @NotBlank(message = "Step ID is required")
        private String id;
        
        @Valid
        private HttpRequestDto request;
        
        private Map<String, String> extract;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public HttpRequestDto getRequest() { return request; }
        public void setRequest(HttpRequestDto request) { this.request = request; }

        public Map<String, String> getExtract() { return extract; }
        public void setExtract(Map<String, String> extract) { this.extract = extract; }

        public AsicCommandStep toTemplateStep() {
            return new AsicCommandStep(
                this.id,
                this.request.toTemplateRequest(),
                this.extract
            );
        }
    }

    public static class HttpRequestDto {
        @NotBlank(message = "HTTP method is required")
        private String method;
        
        @NotBlank(message = "Request path is required")
        private String path;
        
        private Map<String, String> headers;
        private Object body;
        private Integer timeoutMs = 10000;

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

        public AsicHttpRequest toTemplateRequest() {
            return new AsicHttpRequest(
                this.method,
                this.path,
                this.headers,
                this.body,
                this.timeoutMs
            );
        }
    }

    public static class RetryPolicyDto {
        private Integer maxRetries = 2;
        private Integer retryDelayMs = 2000;

        public Integer getMaxRetries() { return maxRetries; }
        public void setMaxRetries(Integer maxRetries) { this.maxRetries = maxRetries; }

        public Integer getRetryDelayMs() { return retryDelayMs; }
        public void setRetryDelayMs(Integer retryDelayMs) { this.retryDelayMs = retryDelayMs; }

        public AsicRetryPolicy toTemplatePolicy() {
            return new AsicRetryPolicy(this.maxRetries, this.retryDelayMs);
        }
    }
}
