package com.cryptoterm.backend.command.domain;

/**
 * Enum для типов ASIC команд.
 * Используется для классификации команд в системе.
 */
public enum CommandType {
    /**
     * Команда перезагрузки майнера.
     * Шаблоны с этим типом должны содержать "reboot" в названии.
     */
    RESTART,

    /**
     * Команда паузы майнинга майнера.
     * Шаблоны с этим типом должны содержать "pause" в названии.
     */
    PAUSE_MINING,

    /**
     * Команда паузы майнинга майнера.
     * Шаблоны с этим типом должны содержать "pause" в названии.
     */
    CONTINUE_MINING,
    
    /**
     * Команда изменения режима работы (мощность-хэшрейт).
     * Шаблоны с этим типом должны содержать паттерн "{мощность}_{хэшрейт}" в названии,
     * например: "1780_5150" (1780W, 51.50 TH/s)
     */
    MODE_CHANGE,
    
    /**
     * Прочие команды (обновление ПО, диагностика и т.д.)
     */
    OTHER;
    
    /**
     * Определяет тип команды по названию шаблона.
     * 
     * @param templateName название шаблона
     * @return тип команды
     */
    public static CommandType fromTemplateName(String templateName) {
        if (templateName == null) {
            return OTHER;
        }
        
        String lowerName = templateName.toLowerCase();
        
        // Проверка на reboot
        if (lowerName.contains("reboot") || lowerName.contains("restart")) {
            return RESTART;
        }

        if (lowerName.contains("continue_mining")) {
            return CONTINUE_MINING;
        }

        if (lowerName.contains("pause_mining")) {
            return PAUSE_MINING;
        }
        
        // Проверка на паттерн изменения режима: два числа через _ или -
        // Примеры: 1780_5150, 2000-6000, 4400W_160TH, eco_mode, standard_5560
        if (lowerName.matches(".*\\d+[wW]?[-_]\\d+[tThH]*.*")) {
            return MODE_CHANGE;
        }
        
        return OTHER;
    }
    
    /**
     * Извлекает мощность из названия шаблона (для MODE_CHANGE).
     * 
     * @param templateName название шаблона
     * @return мощность в ваттах или null если не найдена
     */
    public static Integer extractPowerWatts(String templateName) {
        if (templateName == null) {
            return null;
        }
        
        // Поддерживаем форматы: 1780_5150, 4400W_160TH
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\d+)[wW]?[-_](\\d+)");
        java.util.regex.Matcher matcher = pattern.matcher(templateName);
        
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        
        return null;
    }
    
    /**
     * Извлекает хэшрейт из названия шаблона (для MODE_CHANGE).
     * 
     * @param templateName название шаблона
     * @return хэшрейт (например, 5150 для 51.50 TH/s или 160 для 160 TH/s) или null если не найден
     */
    public static Integer extractHashrate(String templateName) {
        if (templateName == null) {
            return null;
        }
        
        // Поддерживаем форматы: 1780_5150, 4400W_160TH
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\d+)[wW]?[-_](\\d+)");
        java.util.regex.Matcher matcher = pattern.matcher(templateName);
        
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(2));
        }
        
        return null;
    }
}
