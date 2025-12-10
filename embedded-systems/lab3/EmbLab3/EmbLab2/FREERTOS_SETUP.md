# Инструкция по добавлению FreeRTOS в проект

## Шаги для добавления FreeRTOS через STM32CubeMX:

1. **Откройте файл проекта в STM32CubeMX:**
   - Откройте файл `EmbLab2.ioc` в STM32CubeMX

2. **Включите FreeRTOS:**
   - В левой панели найдите раздел "Middleware"
   - Раскройте "Middleware" и найдите "FREERTOS"
   - Выберите "FREERTOS" и включите его (галочка)

3. **Настройте FreeRTOS:**
   - В настройках FreeRTOS выберите "Interface" -> "CMSIS_V1" или "CMSIS_V2"
   - В разделе "Tasks and Queues" можно оставить настройки по умолчанию
   - В разделе "Config parameters" убедитесь что:
     - `configUSE_PREEMPTION` = Enabled
     - `configUSE_IDLE_HOOK` = Disabled
     - `configUSE_TICK_HOOK` = Disabled
     - `configCPU_CLOCK_HZ` = 16000000 (или ваша частота CPU)
     - `configTICK_RATE_HZ` = 1000 (1 мс тик)
     - `configMAX_PRIORITIES` = 7 (минимум)
     - `configMINIMAL_STACK_SIZE` = 128 (или больше)
     - `configTOTAL_HEAP_SIZE` = 8192 (или больше, зависит от ваших задач)

4. **Настройте SysTick для FreeRTOS:**
   - В разделе "System Core" -> "SYS"
   - Убедитесь что "Timebase Source" установлен на "SysTick" (не другой таймер)

5. **Сгенерируйте код:**
   - Нажмите "GENERATE CODE" в STM32CubeMX
   - Это создаст необходимые файлы FreeRTOS в проекте

6. **После генерации кода:**
   - FreeRTOS файлы будут добавлены в проект автоматически
   - Файлы будут находиться в папках `Middlewares/FreeRTOS/`
   - Заголовочные файлы будут доступны через `#include "FreeRTOS.h"`

## Альтернативный способ (если STM32CubeMX недоступен):

Если вы не можете использовать STM32CubeMX, вам нужно вручную добавить FreeRTOS в проект:

1. Скачайте FreeRTOS с официального сайта: https://www.freertos.org/
2. Добавьте исходные файлы FreeRTOS в проект
3. Добавьте пути к заголовочным файлам в настройках компилятора
4. Создайте файл `FreeRTOSConfig.h` с настройками для вашего микроконтроллера

## Важные замечания:

- После добавления FreeRTOS, функция `HAL_Delay()` будет использовать FreeRTOS `vTaskDelay()`
- SysTick будет использоваться FreeRTOS для планировщика задач
- Убедитесь что размер heap достаточен для ваших задач (минимум 4KB рекомендуется)

