package com.cryptoterm.backend.service;

import com.cryptoterm.backend.command.domain.AsicCommandTemplate;
import com.cryptoterm.backend.command.domain.AsicHttpProxyCommand;
import com.cryptoterm.backend.device.domain.Miner;
import com.cryptoterm.backend.command.domain.asic.AsicCommandStep;
import com.cryptoterm.backend.command.domain.asic.AsicConnectionInfo;
import com.cryptoterm.backend.command.domain.asic.AsicHttpRequest;
import com.cryptoterm.backend.command.domain.asic.AsicRetryPolicy;
import com.cryptoterm.backend.command.application.port.out.AsicCommandTemplateRepository;
import com.cryptoterm.backend.device.application.port.out.MinerRepository;
import com.cryptoterm.backend.validation.JsonPathValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сервис для управления шаблонами команд ASIC.
 */
@Service
public class AsicCommandTemplateService {
    private static final Logger log = LoggerFactory.getLogger(AsicCommandTemplateService.class);
    
    private final AsicCommandTemplateRepository templateRepository;
    private final MinerRepository minerRepository;
    private final JsonPathValidator jsonPathValidator;
    
    public AsicCommandTemplateService(AsicCommandTemplateRepository templateRepository,
                                     MinerRepository minerRepository,
                                     JsonPathValidator jsonPathValidator) {
        this.templateRepository = templateRepository;
        this.minerRepository = minerRepository;
        this.jsonPathValidator = jsonPathValidator;
    }
    
    /**
     * Создать новый шаблон команды.
     */
    @Transactional
    public AsicCommandTemplate createTemplate(AsicCommandTemplate template, String createdBy) {
        validateJsonPathExpressions(template);
        
        // Нормализуем имена модели и производителя для консистентности
        if (template.getMinerModel() != null) {
            template.setMinerModel(normalizeModelName(template.getMinerModel()));
        }
        if (template.getMinerVendor() != null) {
            template.setMinerVendor(normalizeModelName(template.getMinerVendor()));
        }
        
        template.setCreatedBy(createdBy);
        template.setCreatedAt(OffsetDateTime.now());
        
        AsicCommandTemplate saved = templateRepository.save(template);
        log.info("Создан шаблон команды '{}' для модели '{}' пользователем '{}'", 
            saved.getName(), saved.getMinerModel(), createdBy);
        
        return saved;
    }
    
    /**
     * Нормализовать имя модели для сравнения.
     * Переводит в нижний регистр и удаляет лишние пробелы.
     */
    private String normalizeModelName(String modelName) {
        if (modelName == null) {
            return "";
        }
        return modelName.trim().toLowerCase().replaceAll("\\s+", " ");
    }
    
    /**
     * Обновить существующий шаблон команды.
     */
    @Transactional
    public Optional<AsicCommandTemplate> updateTemplate(String name, AsicCommandTemplate updatedTemplate) {
        Optional<AsicCommandTemplate> existingOpt = templateRepository.findByName(name);
        
        if (existingOpt.isPresent()) {
            AsicCommandTemplate existing = existingOpt.get();
            
            // Обновить поля
            if (updatedTemplate.getDescription() != null) {
                existing.setDescription(updatedTemplate.getDescription());
            }
            if (updatedTemplate.getMinerModel() != null) {
                existing.setMinerModel(updatedTemplate.getMinerModel());
            }
            if (updatedTemplate.getMinerVendor() != null) {
                existing.setMinerVendor(updatedTemplate.getMinerVendor());
            }
            if (updatedTemplate.getFirmware() != null) {
                existing.setFirmware(updatedTemplate.getFirmware());
            }
            if (updatedTemplate.getSteps() != null) {
                // Validate JSONPath expressions before updating
                validateStepsJsonPath(updatedTemplate.getSteps());
                existing.setSteps(updatedTemplate.getSteps());
            }
            if (updatedTemplate.getPolicy() != null) {
                existing.setPolicy(updatedTemplate.getPolicy());
            }
            
            existing.setUpdatedAt(OffsetDateTime.now());
            
            templateRepository.save(existing);
            log.info("Updated command template '{}'", name);
            
            return Optional.of(existing);
        }
        
        return Optional.empty();
    }
    
    /**
     * Get template by name.
     */
    public Optional<AsicCommandTemplate> getTemplate(String name) {
        return templateRepository.findByName(name);
    }
    
    /**
     * Get all templates.
     */
    public List<AsicCommandTemplate> getAllTemplates() {
        return templateRepository.findAll();
    }
    
    /**
     * Get templates for specific miner model.
     */
    public List<AsicCommandTemplate> getTemplatesForModel(String minerModel) {
        return templateRepository.findByMinerModel(minerModel);
    }
    
    /**
     * Get templates for specific miner vendor and model.
     */
    public List<AsicCommandTemplate> getTemplatesForMinerModelAndVendor(String minerModel, String minerVendor) {
        return templateRepository.findByMinerModelAndMinerVendor(minerModel, minerVendor);
    }
    
    /**
     * Delete a template.
     */
    @Transactional
    public boolean deleteTemplate(String name) {
        if (templateRepository.existsByName(name)) {
            templateRepository.deleteById(name);
            log.info("Deleted command template '{}'", name);
            return true;
        }
        return false;
    }
    
    /**
     * Нормализовать все существующие шаблоны в БД.
     * Приводит miner_model и miner_vendor к нижнему регистру.
     */
    @Transactional
    public int normalizeAllTemplates() {
        List<AsicCommandTemplate> templates = templateRepository.findAll();
        int updatedCount = 0;
        
        for (AsicCommandTemplate template : templates) {
            boolean needsUpdate = false;
            
            if (template.getMinerModel() != null) {
                String normalized = normalizeModelName(template.getMinerModel());
                if (!normalized.equals(template.getMinerModel())) {
                    template.setMinerModel(normalized);
                    needsUpdate = true;
                }
            }
            
            if (template.getMinerVendor() != null) {
                String normalized = normalizeModelName(template.getMinerVendor());
                if (!normalized.equals(template.getMinerVendor())) {
                    template.setMinerVendor(normalized);
                    needsUpdate = true;
                }
            }
            
            if (needsUpdate) {
                templateRepository.save(template);
                updatedCount++;
                log.info("Обновлен шаблон '{}': model='{}', vendor='{}'", 
                    template.getName(), template.getMinerModel(), template.getMinerVendor());
            }
        }
        
        log.info("Нормализация завершена. Обновлено шаблонов: {}", updatedCount);
        return updatedCount;
    }
    
    /**
     * Convert template to executable command for a specific miner.
     * IP address is determined by the device based on miner ID.
     * 
     * @param template The command template
     * @param miner The target miner
     * @return AsicHttpProxyCommand ready to be sent
     */
    public AsicHttpProxyCommand templateToCommand(AsicCommandTemplate template, Miner miner) {
        AsicHttpProxyCommand command = new AsicHttpProxyCommand();
        
        // Set device ID from miner's device
        command.setDeviceId(miner.getDevice().getId().toString());
        
        // Set miner ID (device will determine IP from this)
        command.setMinerId(miner.getId().toString());
        
        // Set ASIC info (firmware for API type detection)
        AsicConnectionInfo asicInfo = new AsicConnectionInfo();
        asicInfo.setFirmware(template.getFirmware());
        asicInfo.setPort(80); // Default port
        asicInfo.setScheme("http"); // Default scheme
        command.setAsic(asicInfo);
        
        // Convert template steps to command steps (already shared type)
        command.setSteps(template.getSteps());
        
        // Set retry policy (already shared type)
        if (template.getPolicy() != null) {
            command.setPolicy(template.getPolicy());
        } else {
            // Default policy
            command.setPolicy(new AsicRetryPolicy(2, 2000));
        }
        
        // Generate command ID
        command.setCmdId(UUID.randomUUID().toString());
        
        return command;
    }
    
    /**
     * Check if template is compatible with miner.
     * Normalizes model names to avoid case sensitivity and spacing issues.
     */
    public boolean isTemplateCompatible(AsicCommandTemplate template, Miner miner) {
        // Normalize and check vendor match
        if (template.getMinerVendor() != null) {
            String templateVendor = normalizeModelName(template.getMinerVendor());
            String minerVendor = normalizeModelName(miner.getVendor());
            
            if (!templateVendor.equals(minerVendor)) {
                log.warn("Template vendor '{}' (normalized: '{}') doesn't match miner vendor '{}' (normalized: '{}')", 
                    template.getMinerVendor(), templateVendor, miner.getVendor(), minerVendor);
                return false;
            }
        }
        
        // Normalize and check model match
        if (template.getMinerModel() != null) {
            String templateModel = normalizeModelName(template.getMinerModel());
            String minerModel = normalizeModelName(miner.getModel());
            
            if (!templateModel.equals(minerModel)) {
                log.warn("Template model '{}' (normalized: '{}') doesn't match miner model '{}' (normalized: '{}')", 
                    template.getMinerModel(), templateModel, miner.getModel(), minerModel);
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Validate JSONPath expressions in template steps.
     */
    private void validateJsonPathExpressions(AsicCommandTemplate template) {
        if (template.getSteps() == null) {
            return;
        }
        validateStepsJsonPath(template.getSteps());
    }
    
    /**
     * Validate JSONPath expressions in a list of steps.
     */
    private void validateStepsJsonPath(List<AsicCommandStep> steps) {
        for (int i = 0; i < steps.size(); i++) {
            AsicCommandStep step = steps.get(i);
            if (step.getExtract() != null) {
                for (var entry : step.getExtract().entrySet()) {
                    String varName = entry.getKey();
                    String jsonPath = entry.getValue();
                    try {
                        jsonPathValidator.validateOrThrow(jsonPath, 
                            String.format("step[%d].extract.%s", i, varName));
                    } catch (IllegalArgumentException e) {
                        log.error("Invalid JSONPath in template: {}", e.getMessage());
                        throw e;
                    }
                }
            }
        }
    }
}
