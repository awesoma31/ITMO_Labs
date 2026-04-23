package com.cryptoterm.backend.dto;

import com.cryptoterm.backend.command.domain.AsicCommandTemplate;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * DTO для ответов шаблонов команд ASIC.
 */
public class AsicCommandTemplateResponse {

    private String name;
    private String description;
    private String minerModel;
    private String minerVendor;
    private String firmware;
    private Integer stepsCount;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String createdBy;

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

    public Integer getStepsCount() { return stepsCount; }
    public void setStepsCount(Integer stepsCount) { this.stepsCount = stepsCount; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    /**
     * Преобразует сущность в DTO
     */
    public static AsicCommandTemplateResponse fromEntity(AsicCommandTemplate template) {
        AsicCommandTemplateResponse response = new AsicCommandTemplateResponse();
        response.setName(template.getName());
        response.setDescription(template.getDescription());
        response.setMinerModel(template.getMinerModel());
        response.setMinerVendor(template.getMinerVendor());
        response.setFirmware(template.getFirmware());
        response.setStepsCount(template.getSteps() != null ? template.getSteps().size() : 0);
        response.setCreatedAt(template.getCreatedAt());
        response.setUpdatedAt(template.getUpdatedAt());
        response.setCreatedBy(template.getCreatedBy());
        return response;
    }
}
