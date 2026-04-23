package com.cryptoterm.backend.command.application.factory;

import com.cryptoterm.backend.command.domain.AsicCommandTemplate;
import com.cryptoterm.backend.command.domain.AsicHttpProxyCommand;
import com.cryptoterm.backend.command.domain.CommandType;
import com.cryptoterm.backend.command.domain.PowerMode;
import com.cryptoterm.backend.device.domain.Miner;

import java.util.List;

/**
 * Абстрактная фабрика для создания ASIC команд.
 * 
 * Каждая реализация фабрики специфична для конкретной модели майнера
 * и умеет создавать команды на основе шаблонов для этой модели.
 */
public interface AsicCommandFactory {
    
    /**
     * Получить название модели майнера, которую поддерживает эта фабрика.
     * 
     * @return название модели (например, "Antminer S19 Pro Hydro")
     */
    String getSupportedMinerModel();
    
    /**
     * Получить вендора майнера, которого поддерживает эта фабрика.
     * 
     * @return название вендора (например, "Bitmain")
     */
    String getSupportedMinerVendor();
    
    /**
     * Проверить, поддерживает ли фабрика данный майнер.
     * 
     * @param miner майнер для проверки
     * @return true если фабрика поддерживает этот майнер
     */
    default boolean supports(Miner miner) {
        String minerModel = miner.getModel();
        String minerVendor = miner.getVendor();
        
        if (minerModel == null || minerVendor == null) {
            return false;
        }
        
        // Если vendor - это просто "default", игнорируем его при сравнении
        boolean vendorMatches;
        if ("default".equalsIgnoreCase(minerVendor.trim())) {
            // Если vendor = "default", то проверяем только модель
            vendorMatches = true;
        } else {
            // Удаляем префикс "default " если есть (игнорируем регистр)
            if (minerVendor.toLowerCase().startsWith("default ")) {
                minerVendor = minerVendor.substring(8); // "default ".length() = 8
            }
            vendorMatches = minerVendor.equalsIgnoreCase(getSupportedMinerVendor());
        }
        
        // Удаляем префикс "default " из модели если есть
        if (minerModel.toLowerCase().startsWith("default ")) {
            minerModel = minerModel.substring(8);
        }
        
        boolean modelMatches = minerModel.equalsIgnoreCase(getSupportedMinerModel());
        
        return vendorMatches && modelMatches;
    }
    
    /**
     * Создать команду перезагрузки для майнера.
     * 
     * @param miner целевой майнер
     * @param templates список доступных шаблонов для этой модели
     * @return команда перезагрузки или null если шаблон не найден
     */
    AsicHttpProxyCommand createRebootCommand(
        Miner miner, 
        List<AsicCommandTemplate> templates
    );

    /**
     * Создать команду паузы майнинга для майнера.
     *
     * @param miner целевой майнер
     * @param templates список доступных шаблонов для этой модели
     * @return команда перезагрузки или null если шаблон не найден
     */
    AsicHttpProxyCommand createPauseCommand(
            Miner miner,
            List<AsicCommandTemplate> templates
    );

    /**
     * Создать команду паузы майнинга для майнера.
     *
     * @param miner целевой майнер
     * @param templates список доступных шаблонов для этой модели
     * @return команда перезагрузки или null если шаблон не найден
     */
    AsicHttpProxyCommand createContinueCommand(
            Miner miner,
            List<AsicCommandTemplate> templates
    );
    
    /**
     * Создать команду изменения режима работы.
     * 
     * @param miner целевой майнер
     * @param mode режим работы (ECO, STANDARD, OVERCLOCK)
     * @param templates список доступных шаблонов для этой модели
     * @return команда изменения режима или null если шаблон не найден
     */
    AsicHttpProxyCommand createModeChangeCommand(
        Miner miner,
        PowerMode mode,
        List<AsicCommandTemplate> templates
    );
    
    /**
     * Создать команду изменения режима работы с явным указанием мощности и хэшрейта.
     * 
     * @param miner целевой майнер
     * @param powerWatts мощность в ваттах
     * @param hashrate хэшрейт (например, 5150 для 51.50 TH/s)
     * @param templates список доступных шаблонов для этой модели
     * @return команда изменения режима или null если шаблон не найден
     */
    AsicHttpProxyCommand createModeChangeCommand(
        Miner miner,
        int powerWatts,
        int hashrate,
        List<AsicCommandTemplate> templates
    );
    
    /**
     * Найти шаблон по типу команды.
     * 
     * @param templates список шаблонов
     * @param commandType тип команды
     * @return первый найденный шаблон или null
     */
    default AsicCommandTemplate findTemplateByType(
        List<AsicCommandTemplate> templates,
        CommandType commandType
    ) {
        return templates.stream()
            .filter(t -> CommandType.fromTemplateName(t.getName()) == commandType)
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Найти шаблон по мощности и хэшрейту.
     * 
     * @param templates список шаблонов
     * @param powerWatts мощность в ваттах
     * @param hashrate хэшрейт
     * @return шаблон с точным совпадением или null
     */
    default AsicCommandTemplate findTemplateByPowerAndHashrate(
        List<AsicCommandTemplate> templates,
        int powerWatts,
        int hashrate
    ) {
        return templates.stream()
            .filter(t -> {
                Integer tPower = CommandType.extractPowerWatts(t.getName());
                Integer tHashrate = CommandType.extractHashrate(t.getName());
                return tPower != null && tPower == powerWatts &&
                       tHashrate != null && tHashrate == hashrate;
            })
            .findFirst()
            .orElse(null);
    }
}
