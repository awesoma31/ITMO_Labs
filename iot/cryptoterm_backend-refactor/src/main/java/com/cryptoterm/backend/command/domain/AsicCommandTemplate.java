package com.cryptoterm.backend.command.domain;

import com.cryptoterm.backend.command.domain.asic.AsicCommandStep;
import com.cryptoterm.backend.command.domain.asic.AsicRetryPolicy;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Шаблон для ASIC команд, хранящихся в MongoDB.
 * Шаблоны определяют переиспользуемые последовательности команд для конкретных моделей майнеров и профилей мощности.
 * 
 * Пример: шаблон "1780_watts" для Antminer S19 Pro Hydro
 */
@Document(collection = "asic_command_templates")
public class AsicCommandTemplate {

    @Id
    private String name; // Используется как ID, например, "1780_watts", "2000_watts"

    @Field("description")
    private String description;

    @Field("miner_model")
    @Indexed
    private String minerModel; // например, "Antminer S19 Pro Hydro"

    @Field("miner_vendor")
    @Indexed
    private String minerVendor; // например, "Bitmain"

    @Field("firmware")
    private String firmware; // например, "anthill", "stock", "vnish"

    @Field("steps")
    private List<AsicCommandStep> steps;

    @Field("policy")
    private AsicRetryPolicy policy;

    @Field("created_at")
    private OffsetDateTime createdAt;

    @Field("updated_at")
    private OffsetDateTime updatedAt;

    @Field("created_by")
    private String createdBy; // Имя пользователя администратора, создавшего этот шаблон
    
    @Field("command_type")
    private CommandType commandType; // Тип команды (автоматически определяется по названию)

    // Конструкторы
    public AsicCommandTemplate() {
        this.createdAt = OffsetDateTime.now();
    }

    public AsicCommandTemplate(String name, String description, String minerModel, 
                              String minerVendor, String firmware, 
                              List<AsicCommandStep> steps, AsicRetryPolicy policy) {
        this();
        this.name = name;
        this.description = description;
        this.minerModel = minerModel;
        this.minerVendor = minerVendor;
        this.firmware = firmware;
        this.steps = steps;
        this.policy = policy;
    }

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

    public List<AsicCommandStep> getSteps() { return steps; }
    public void setSteps(List<AsicCommandStep> steps) { this.steps = steps; }

    public AsicRetryPolicy getPolicy() { return policy; }
    public void setPolicy(AsicRetryPolicy policy) { this.policy = policy; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public CommandType getCommandType() { 
        // Автоматическое определение по названию, если не установлено
        if (commandType == null && name != null) {
            return CommandType.fromTemplateName(name);
        }
        return commandType; 
    }
    
    public void setCommandType(CommandType commandType) { 
        this.commandType = commandType; 
    }
}
