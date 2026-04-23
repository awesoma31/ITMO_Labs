package com.cryptoterm.backend.command.domain;

import com.cryptoterm.backend.command.domain.asic.AsicCommandStep;
import com.cryptoterm.backend.command.domain.asic.AsicConnectionInfo;
import com.cryptoterm.backend.command.domain.asic.AsicRetryPolicy;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Domain модель для ASIC HTTP Proxy команд, хранящихся в MongoDB.
 * Эти команды отправляются на устройства Raspberry Pi через MQTT для проксирования HTTP запросов к ASIC майнерам.
 */
@Document(collection = "asic_commands")
public class AsicHttpProxyCommand {

    @Id
    private String cmdId;

    @Field("device_id")
    private String deviceId;
    
    @Field("miner_id")
    private String minerId; // ID майнера (RP знает IP по этому ID)

    @Field("asic")
    private AsicConnectionInfo asic;

    @Field("steps")
    private List<AsicCommandStep> steps;

    @Field("policy")
    private AsicRetryPolicy policy;

    @Field("signature")
    private String signature;

    @Field("status")
    private CommandStatus status;

    @Field("created_at")
    private OffsetDateTime createdAt;

    @Field("updated_at")
    private OffsetDateTime updatedAt;

    @Field("executed_at")
    private OffsetDateTime executedAt;

    @Field("scheduled_at")
    private OffsetDateTime scheduledAt;

    @Field("power_mode")
    private String powerMode;

    @Field("result")
    private CommandResult result;

    // Конструкторы
    public AsicHttpProxyCommand() {
        this.createdAt = OffsetDateTime.now();
        this.status = CommandStatus.PENDING;
    }

    public AsicHttpProxyCommand(String cmdId, String deviceId, AsicConnectionInfo asic, 
                                List<AsicCommandStep> steps, AsicRetryPolicy policy) {
        this();
        this.cmdId = cmdId;
        this.deviceId = deviceId;
        this.asic = asic;
        this.steps = steps;
        this.policy = policy;
    }

    // Геттеры и сеттеры
    public String getCmdId() { return cmdId; }
    public void setCmdId(String cmdId) { this.cmdId = cmdId; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    
    public String getMinerId() { return minerId; }
    public void setMinerId(String minerId) { this.minerId = minerId; }

    public AsicConnectionInfo getAsic() { return asic; }
    public void setAsic(AsicConnectionInfo asic) { this.asic = asic; }

    public List<AsicCommandStep> getSteps() { return steps; }
    public void setSteps(List<AsicCommandStep> steps) { this.steps = steps; }

    public AsicRetryPolicy getPolicy() { return policy; }
    public void setPolicy(AsicRetryPolicy policy) { this.policy = policy; }

    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }

    public CommandStatus getStatus() { return status; }
    public void setStatus(CommandStatus status) { 
        this.status = status;
        this.updatedAt = OffsetDateTime.now();
    }

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

    public CommandResult getResult() { return result; }
    public void setResult(CommandResult result) { this.result = result; }

    // Вложенные классы для результатов выполнения команд
    public static class CommandResult {
        private String status;
        private String failedStep;
        private List<StepResult> stepResults;

        public CommandResult() {}

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getFailedStep() { return failedStep; }
        public void setFailedStep(String failedStep) { this.failedStep = failedStep; }

        public List<StepResult> getStepResults() { return stepResults; }
        public void setStepResults(List<StepResult> stepResults) { this.stepResults = stepResults; }
    }

    public static class StepResult {
        private String stepId;
        private String status;
        private Integer statusCode;
        private String responsePreview;
        private String error;

        public StepResult() {}

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

    public enum CommandStatus {
        PENDING,    // Команда создана, еще не отправлена
        SCHEDULED,  // Команда запланирована на выполнение в scheduledAt
        SENT,       // Команда отправлена на устройство через MQTT
        EXECUTING,  // Устройство выполняет команду
        SUCCESS,    // Команда выполнена успешно
        FAILED,     // Выполнение команды завершилось ошибкой
        CANCELLED   // Команда была отменена
    }
}
