// src/useAutoRefresh.js (НОВЫЙ ФАЙЛ)

import { useEffect } from 'react';

// Хук для выполнения callback-функции с заданным интервалом
export default function useAutoRefresh(callback, interval, dependencies = []) {
    useEffect(() => {
        // Инициализация: первый вызов
        callback(); 

        const timer = setInterval(() => {
            callback();
        }, interval);

        // Cleanup: очистка таймера при unmount или изменении зависимостей
        return () => clearInterval(timer);
    }, dependencies); // Перезапускаем таймер при изменении зависимостей
}
