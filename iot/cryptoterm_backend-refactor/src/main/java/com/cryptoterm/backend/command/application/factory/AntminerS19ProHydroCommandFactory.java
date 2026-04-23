package com.cryptoterm.backend.command.application.factory;

import com.cryptoterm.backend.command.domain.AsicCommandTemplate;
import com.cryptoterm.backend.command.domain.AsicHttpProxyCommand;
import com.cryptoterm.backend.command.domain.CommandType;
import com.cryptoterm.backend.command.domain.PowerMode;
import com.cryptoterm.backend.command.domain.asic.AsicConnectionInfo;
import com.cryptoterm.backend.command.domain.asic.AsicRetryPolicy;
import com.cryptoterm.backend.device.domain.Miner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Фабрика команд для Antminer S19 Pro Hydro от Bitmain.
 * 
 * Поддерживаемые команды:
 * - Перезагрузка (reboot)
 * - Изменение режима работы с различными профилями мощности
 * 
 * Режимы работы определяются автоматически на основе доступных шаблонов:
 * - ECO: минимальная мощность (0-й элемент в отсортированном списке)
 * - STANDARD: средняя мощность (средний элемент)
 * - OVERCLOCK: максимальная мощность (предпоследний элемент)
 */
@Component
public class AntminerS19ProHydroCommandFactory implements AsicCommandFactory {
    private static final Logger log = LoggerFactory.getLogger(AntminerS19ProHydroCommandFactory.class);
    
    private static final String SUPPORTED_VENDOR = "Bitmain";
    private static final String SUPPORTED_MODEL = "Antminer S19 Pro Hydro";
    
    @Override
    public String getSupportedMinerModel() {
        return SUPPORTED_MODEL;
    }
    
    @Override
    public String getSupportedMinerVendor() {
        return SUPPORTED_VENDOR;
    }
    
    @Override
    public AsicHttpProxyCommand createRebootCommand(
            Miner miner,
            List<AsicCommandTemplate> templates) {
        
        log.debug("Creating reboot command for miner {}", miner.getId());
        
        // Найти шаблон reboot
        AsicCommandTemplate rebootTemplate = findTemplateByType(templates, CommandType.RESTART);
        
        if (rebootTemplate == null) {
            log.error("Reboot template not found for {}", SUPPORTED_MODEL);
            return null;
        }
        
        return buildCommand(miner, rebootTemplate);
    }

    @Override
    public AsicHttpProxyCommand createPauseCommand(
            Miner miner,
            List<AsicCommandTemplate> templates) {

        log.debug("Creating pause command for miner {}", miner.getId());

        // Найти шаблон pause
        AsicCommandTemplate pauseTemplate = findTemplateByType(templates, CommandType.PAUSE_MINING);

        if (pauseTemplate == null) {
            log.error("Reboot template not found for {}", SUPPORTED_MODEL);
            return null;
        }

        return buildCommand(miner, pauseTemplate);
    }

    @Override
    public AsicHttpProxyCommand createContinueCommand(
            Miner miner,
            List<AsicCommandTemplate> templates) {

        log.debug("Creating continue command for miner {}", miner.getId());

        // Найти шаблон pause
        AsicCommandTemplate pauseTemplate = findTemplateByType(templates, CommandType.CONTINUE_MINING);

        if (pauseTemplate == null) {
            log.error("Reboot template not found for {}", SUPPORTED_MODEL);
            return null;
        }

        return buildCommand(miner, pauseTemplate);
    }
    
    @Override
    public AsicHttpProxyCommand createModeChangeCommand(
            Miner miner,
            PowerMode mode,
            List<AsicCommandTemplate> templates) {
        
        log.debug("Creating mode change command for miner {} - mode: {}", 
            miner.getId(), mode);
        
        // Найти подходящий шаблон для режима
        AsicCommandTemplate template = findTemplateForPowerMode(templates, mode);
        
        if (template == null) {
            log.error("Template not found for mode {} on {}", mode, SUPPORTED_MODEL);
            return null;
        }
        
        return buildCommand(miner, template);
    }
    
    @Override
    public AsicHttpProxyCommand createModeChangeCommand(
            Miner miner,
            int powerWatts,
            int hashrate,
            List<AsicCommandTemplate> templates) {
        
        log.debug("Creating mode change command for miner {} - {}W, {} TH/s", 
            miner.getId(), powerWatts, hashrate);
        
        // Сначала попытаться найти точное совпадение
        AsicCommandTemplate exactMatch = findTemplateByPowerAndHashrate(
            templates, powerWatts, hashrate
        );
        
        if (exactMatch != null) {
            return buildCommand(miner, exactMatch);
        }
        
        // Если точного совпадения нет, найти ближайший по мощности
        AsicCommandTemplate closestTemplate = findClosestTemplateByPower(templates, powerWatts);
        
        if (closestTemplate == null) {
            log.error("No suitable template found for {}W on {}", powerWatts, SUPPORTED_MODEL);
            return null;
        }
        
        log.info("Using closest template '{}' for {}W", closestTemplate.getName(), powerWatts);
        return buildCommand(miner, closestTemplate);
    }
    
    /**
     * Найти шаблон для конкретного режима работы.
     * Логика: сортируем по мощности, ECO = первый, STANDARD = средний, OVERCLOCK = предпоследний.
     */
    private AsicCommandTemplate findTemplateForPowerMode(
            List<AsicCommandTemplate> templates,
            PowerMode mode) {
        
        log.info("=== Finding template for mode {} ===", mode);
        log.info("Total templates available: {}", templates.size());
        
        // Логируем все доступные шаблоны
        templates.forEach(t -> {
            CommandType type = CommandType.fromTemplateName(t.getName());
            Integer power = CommandType.extractPowerWatts(t.getName());
            log.info("Template: '{}', Type: {}, Power: {}", t.getName(), type, power);
        });
        
        // Фильтруем только шаблоны изменения режима с известной мощностью
        List<AsicCommandTemplate> modeTemplates = templates.stream()
            .filter(t -> CommandType.fromTemplateName(t.getName()) == CommandType.MODE_CHANGE)
            .filter(t -> CommandType.extractPowerWatts(t.getName()) != null)
            .sorted(Comparator.comparing(t -> CommandType.extractPowerWatts(t.getName())))
            .toList();
        
        log.info("Filtered MODE_CHANGE templates: {}", modeTemplates.size());
        modeTemplates.forEach(t -> 
            log.info("MODE_CHANGE template: '{}', Power: {}W", 
                t.getName(), CommandType.extractPowerWatts(t.getName()))
        );
        
        if (modeTemplates.isEmpty()) {
            log.error("No MODE_CHANGE templates found!");
            return null;
        }
        
        switch (mode) {
            case ECO:
                // Первый элемент (минимальная мощность)
                log.info("Selecting ECO mode: index 0");
                return modeTemplates.get(0);
                    
            case STANDARD:
                // Средний элемент
                int middleIndex = modeTemplates.size() / 2;
                log.info("Selecting STANDARD mode: index {}", middleIndex);
                return modeTemplates.get(middleIndex);
                    
            case OVERCLOCK:
                // Предпоследний элемент (или последний если только один элемент)
                int overclockIndex = Math.max(0, modeTemplates.size() - 2);
                if (modeTemplates.size() == 1) {
                    overclockIndex = 0;
                }
                log.info("Selecting OVERCLOCK mode: index {} (size: {})", overclockIndex, modeTemplates.size());
                return modeTemplates.get(overclockIndex);
                    
            default:
                log.error("Unknown mode: {}", mode);
                return null;
        }
    }
    
    /**
     * Найти ближайший шаблон по мощности.
     */
    private AsicCommandTemplate findClosestTemplateByPower(
            List<AsicCommandTemplate> templates,
            int targetPower) {
        
        return templates.stream()
            .filter(t -> CommandType.fromTemplateName(t.getName()) == CommandType.MODE_CHANGE)
            .filter(t -> CommandType.extractPowerWatts(t.getName()) != null)
            .min(Comparator.comparing(t -> 
                Math.abs(CommandType.extractPowerWatts(t.getName()) - targetPower)
            ))
            .orElse(null);
    }
    
    /**
     * Построить команду из шаблона.
     */
    private AsicHttpProxyCommand buildCommand(
            Miner miner,
            AsicCommandTemplate template) {
        
        AsicHttpProxyCommand command = new AsicHttpProxyCommand();
        
        // Генерация ID команды
        command.setCmdId(UUID.randomUUID().toString());
        
        // Устройство
        command.setDeviceId(miner.getDevice().getId().toString());
        
        // ID майнера (RP определит IP по этому ID)
        command.setMinerId(miner.getId().toString());
        
        // Информация о ASIC (firmware для определения типа API)
        AsicConnectionInfo asicInfo = new AsicConnectionInfo();
        asicInfo.setFirmware(template.getFirmware() != null ? 
            template.getFirmware() : "anthill");
        asicInfo.setPort(80); // Стандартный порт для Antminer
        asicInfo.setScheme("http");
        command.setAsic(asicInfo);
        
        // Шаги выполнения из шаблона
        command.setSteps(template.getSteps());
        
        // Политика повторов
        if (template.getPolicy() != null) {
            command.setPolicy(template.getPolicy());
        } else {
            // Политика по умолчанию
            command.setPolicy(new AsicRetryPolicy(2, 2000));
        }
        
        log.info("Built command {} from template '{}' for miner {}", 
            command.getCmdId(), template.getName(), miner.getId());
        
        return command;
    }
}
