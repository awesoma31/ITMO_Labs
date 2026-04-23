package com.cryptoterm.backend.command.domain;

/**
 * Режимы работы майнера.
 */
public enum PowerMode {
    /**
     * Экономичный режим - низкое энергопотребление
     */
    ECO,
    
    /**
     * Стандартный режим - баланс между производительностью и энергопотреблением
     */
    STANDARD,
    
    /**
     * Режим разгона - максимальная производительность
     */
    OVERCLOCK;
    
    /**
     * Определяет режим работы по мощности для конкретной модели майнера.
     * 
     * @param powerWatts мощность в ваттах
     * @param minerModel модель майнера
     * @return режим работы
     */
    public static PowerMode fromPower(int powerWatts, String minerModel) {
        // Пороги для Antminer S19 Pro Hydro
        if (minerModel != null && minerModel.contains("S19 Pro Hydro")) {
            if (powerWatts <= 1800) {
                return ECO;
            } else if (powerWatts <= 1900) {
                return STANDARD;
            } else {
                return OVERCLOCK;
            }
        }
        
        // Общие пороги по умолчанию
        if (powerWatts <= 1500) {
            return ECO;
        } else if (powerWatts <= 2000) {
            return STANDARD;
        } else {
            return OVERCLOCK;
        }
    }
}
