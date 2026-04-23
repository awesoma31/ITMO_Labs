package com.cryptoterm.backend.mqtt.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * DTO for command response messages received from Raspberry Pi devices via MQTT.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommandResponseMessage {
    private String cmdId;
    private String status;
    private String failedStep;
    private List<StepResultDto> stepResults;

    public CommandResponseMessage() {}

    public String getCmdId() { return cmdId; }
    public void setCmdId(String cmdId) { this.cmdId = cmdId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getFailedStep() { return failedStep; }
    public void setFailedStep(String failedStep) { this.failedStep = failedStep; }

    public List<StepResultDto> getStepResults() { return stepResults; }
    public void setStepResults(List<StepResultDto> stepResults) { this.stepResults = stepResults; }

    public static class StepResultDto {
        private String stepId;
        private String status;
        private Integer statusCode;
        private String responsePreview;
        private String error;

        public StepResultDto() {}

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
