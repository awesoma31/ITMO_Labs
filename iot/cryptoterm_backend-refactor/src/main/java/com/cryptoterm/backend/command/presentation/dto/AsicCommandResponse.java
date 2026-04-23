package com.cryptoterm.backend.dto;

import com.cryptoterm.backend.command.domain.AsicHttpProxyCommand;
import com.cryptoterm.backend.command.domain.asic.AsicCommandStep;
import com.cryptoterm.backend.command.domain.asic.AsicConnectionInfo;
import com.cryptoterm.backend.command.domain.asic.AsicHttpRequest;
import com.cryptoterm.backend.command.domain.asic.AsicRetryPolicy;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * DTO для ответа команды ASIC HTTP Proxy.
 */
public class AsicCommandResponse {
    @JsonProperty("cmd_id")
    private String cmdId;
    @JsonProperty("device_id")
    private String deviceId;
    private AsicInfoDto asic;
    private List<CommandStepDto> steps;
    private RetryPolicyDto policy;
    private String signature;
    private String status;
    @JsonProperty("created_at")
    private OffsetDateTime createdAt;
    @JsonProperty("updated_at")
    private OffsetDateTime updatedAt;
    @JsonProperty("executed_at")
    private OffsetDateTime executedAt;
    @JsonProperty("scheduled_at")
    private OffsetDateTime scheduledAt;
    @JsonProperty("power_mode")
    private String powerMode;
    private CommandResultDto result;

    public AsicCommandResponse() {}

    // Статический фабричный метод для создания из сущности
    public static AsicCommandResponse fromEntity(AsicHttpProxyCommand command) {
        AsicCommandResponse response = new AsicCommandResponse();
        response.setCmdId(command.getCmdId());
        response.setDeviceId(command.getDeviceId());
        
        if (command.getAsic() != null) {
            response.setAsic(AsicInfoDto.fromEntity(command.getAsic()));
        }
        
        if (command.getSteps() != null) {
            response.setSteps(command.getSteps().stream()
                .map(CommandStepDto::fromEntity)
                .toList());
        }
        
        if (command.getPolicy() != null) {
            response.setPolicy(RetryPolicyDto.fromEntity(command.getPolicy()));
        }
        
        response.setSignature(command.getSignature());
        response.setStatus(command.getStatus() != null ? command.getStatus().name() : null);
        response.setCreatedAt(command.getCreatedAt());
        response.setUpdatedAt(command.getUpdatedAt());
        response.setExecutedAt(command.getExecutedAt());
        response.setScheduledAt(command.getScheduledAt());
        response.setPowerMode(command.getPowerMode());

        if (command.getResult() != null) {
            response.setResult(CommandResultDto.fromEntity(command.getResult()));
        }
        
        return response;
    }

    // Геттеры и сеттеры
    public String getCmdId() { return cmdId; }
    public void setCmdId(String cmdId) { this.cmdId = cmdId; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public AsicInfoDto getAsic() { return asic; }
    public void setAsic(AsicInfoDto asic) { this.asic = asic; }

    public List<CommandStepDto> getSteps() { return steps; }
    public void setSteps(List<CommandStepDto> steps) { this.steps = steps; }

    public RetryPolicyDto getPolicy() { return policy; }
    public void setPolicy(RetryPolicyDto policy) { this.policy = policy; }

    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }

    public OffsetDateTime getExecutedAt() { return executedAt; }
    public void setExecutedAt(OffsetDateTime executedAt) { this.executedAt = executedAt; }

    public OffsetDateTime getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(OffsetDateTime scheduledAt) { this.scheduledAt = scheduledAt; }

    public String getPowerMode() { return powerMode; }
    public void setPowerMode(String powerMode) { this.powerMode = powerMode; }

    public CommandResultDto getResult() { return result; }
    public void setResult(CommandResultDto result) { this.result = result; }

    // Вложенные DTO
    public static class AsicInfoDto {
        private String firmware;
        private Integer port;
        private String scheme;

        public static AsicInfoDto fromEntity(AsicConnectionInfo asic) {
            AsicInfoDto dto = new AsicInfoDto();
            dto.setFirmware(asic.getFirmware());
            dto.setPort(asic.getPort());
            dto.setScheme(asic.getScheme());
            return dto;
        }

        public String getFirmware() { return firmware; }
        public void setFirmware(String firmware) { this.firmware = firmware; }

        public Integer getPort() { return port; }
        public void setPort(Integer port) { this.port = port; }

        public String getScheme() { return scheme; }
        public void setScheme(String scheme) { this.scheme = scheme; }
    }

    public static class CommandStepDto {
        private String id;
        private HttpRequestDto request;
        private Object extract;

        public static CommandStepDto fromEntity(AsicCommandStep step) {
            CommandStepDto dto = new CommandStepDto();
            dto.setId(step.getId());
            if (step.getRequest() != null) {
                dto.setRequest(HttpRequestDto.fromEntity(step.getRequest()));
            }
            dto.setExtract(step.getExtract());
            return dto;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public HttpRequestDto getRequest() { return request; }
        public void setRequest(HttpRequestDto request) { this.request = request; }

        public Object getExtract() { return extract; }
        public void setExtract(Object extract) { this.extract = extract; }
    }

    public static class HttpRequestDto {
        private String method;
        private String path;
        private Object headers;
        private Object body;
        @JsonProperty("timeout_ms")
        private Integer timeoutMs;

        public static HttpRequestDto fromEntity(AsicHttpRequest request) {
            HttpRequestDto dto = new HttpRequestDto();
            dto.setMethod(request.getMethod());
            dto.setPath(request.getPath());
            dto.setHeaders(request.getHeaders());
            dto.setBody(request.getBody());
            dto.setTimeoutMs(request.getTimeoutMs());
            return dto;
        }

        public String getMethod() { return method; }
        public void setMethod(String method) { this.method = method; }

        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }

        public Object getHeaders() { return headers; }
        public void setHeaders(Object headers) { this.headers = headers; }

        public Object getBody() { return body; }
        public void setBody(Object body) { this.body = body; }

        public Integer getTimeoutMs() { return timeoutMs; }
        public void setTimeoutMs(Integer timeoutMs) { this.timeoutMs = timeoutMs; }
    }

    public static class RetryPolicyDto {
        @JsonProperty("max_retries")
        private Integer maxRetries;
        @JsonProperty("retry_delay_ms")
        private Integer retryDelayMs;

        public static RetryPolicyDto fromEntity(AsicRetryPolicy policy) {
            RetryPolicyDto dto = new RetryPolicyDto();
            dto.setMaxRetries(policy.getMaxRetries());
            dto.setRetryDelayMs(policy.getRetryDelayMs());
            return dto;
        }

        public Integer getMaxRetries() { return maxRetries; }
        public void setMaxRetries(Integer maxRetries) { this.maxRetries = maxRetries; }

        public Integer getRetryDelayMs() { return retryDelayMs; }
        public void setRetryDelayMs(Integer retryDelayMs) { this.retryDelayMs = retryDelayMs; }
    }

    public static class CommandResultDto {
        private String status;
        @JsonProperty("failed_step")
        private String failedStep;
        @JsonProperty("step_results")
        private List<StepResultDto> stepResults;

        public static CommandResultDto fromEntity(AsicHttpProxyCommand.CommandResult result) {
            CommandResultDto dto = new CommandResultDto();
            dto.setStatus(result.getStatus());
            dto.setFailedStep(result.getFailedStep());
            if (result.getStepResults() != null) {
                dto.setStepResults(result.getStepResults().stream()
                    .map(StepResultDto::fromEntity)
                    .toList());
            }
            return dto;
        }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getFailedStep() { return failedStep; }
        public void setFailedStep(String failedStep) { this.failedStep = failedStep; }

        public List<StepResultDto> getStepResults() { return stepResults; }
        public void setStepResults(List<StepResultDto> stepResults) { this.stepResults = stepResults; }
    }

    public static class StepResultDto {
        @JsonProperty("step_id")
        private String stepId;
        private String status;
        @JsonProperty("status_code")
        private Integer statusCode;
        @JsonProperty("response_preview")
        private String responsePreview;
        private String error;

        public static StepResultDto fromEntity(AsicHttpProxyCommand.StepResult step) {
            StepResultDto dto = new StepResultDto();
            dto.setStepId(step.getStepId());
            dto.setStatus(step.getStatus());
            dto.setStatusCode(step.getStatusCode());
            dto.setResponsePreview(step.getResponsePreview());
            dto.setError(step.getError());
            return dto;
        }

        public String getStepId() { return stepId; }
        public void setStepId(String stepId) { this.stepId = stepId; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public Integer getStatusCode() { return statusCode; }
        public void setStatusCode(Integer statusCode) { this.statusCode = statusCode; }

        public String getResponsePreview() { return responsePreview; }
        public void setResponsePreview(String responsePreview) { this.responsePreview = responsePreview; }

        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }
}
