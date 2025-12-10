/* USER CODE BEGIN Header */
/**
  ******************************************************************************
  * @file           : main.c
  * @brief          : Main program body
  ******************************************************************************
  * @attention
  * Copyright (c) 2025 STMicroelectronics.
  * All rights reserved.
  * This software is licensed under terms that can be found in the LICENSE file
  * in the root directory of this software component.
  * If no LICENSE file comes with this software, it is provided AS-IS.
  ******************************************************************************
  */
/* USER CODE END Header */
/* Includes ------------------------------------------------------------------*/
#include "main.h"
#include "cmsis_os.h"
#include "i2c.h"
#include "tim.h"
#include "usart.h"
#include "gpio.h"

/* Private includes ----------------------------------------------------------*/
/* USER CODE BEGIN Includes */
#include <stdio.h>
#include <string.h>
#include <inttypes.h>
#include "oled.h"
#include "sdk_uart.h"
#include "kb.h"
#include "tetris.h"
#include "buzzer.h"
#include "FreeRTOS.h"
#include "task.h"
#include "queue.h"
#include "semphr.h"
/* USER CODE END Includes */

/* Private typedef -----------------------------------------------------------*/
/* USER CODE BEGIN PTD */

/* USER CODE END PTD */

/* Private define ------------------------------------------------------------*/
/* USER CODE BEGIN PD */
/* USER CODE END PD */

/* Private macro -------------------------------------------------------------*/
/* USER CODE BEGIN PM */

/* USER CODE END PM */

/* Private variables ---------------------------------------------------------*/

/* USER CODE BEGIN PV */
// Глобальная структура игры
static TetrisGame tetris_game;

// Очереди и семафоры для синхронизации задач
static QueueHandle_t key_queue = NULL;
static SemaphoreHandle_t display_mutex = NULL;
static SemaphoreHandle_t game_mutex = NULL;

// Переменная для управления звуком
static uint32_t sound_off_time = 0;
/* USER CODE END PV */

/* Private function prototypes -----------------------------------------------*/
void SystemClock_Config(void);
void MX_FREERTOS_Init(void);
/* USER CODE BEGIN PFP */
uint8_t Get_KeyCode(void);
void vKeyboardTask(void *pvParameters);
void vGameTask(void *pvParameters);
void vDisplayTask(void *pvParameters);
/* USER CODE END PFP */

/* Private user code ---------------------------------------------------------*/
/* USER CODE BEGIN 0 */
/* USER CODE END 0 */

/**
  * @brief  The application entry point.
  * @retval int
  */
int main(void)
{

  /* USER CODE BEGIN 1 */

  /* USER CODE END 1 */

  /* MCU Configuration--------------------------------------------------------*/

  /* Reset of all peripherals, Initializes the Flash interface and the Systick. */
  HAL_Init();

  /* USER CODE BEGIN Init */

  /* USER CODE END Init */

  /* Configure the system clock */
  SystemClock_Config();

  /* USER CODE BEGIN SysInit */

  /* USER CODE END SysInit */

  /* Initialize all configured peripherals */
  MX_GPIO_Init();
  MX_I2C1_Init();
  MX_USART6_UART_Init();
  MX_TIM2_Init();
  /* USER CODE BEGIN 2 */
  oled_Init();
  UART_Transmit((uint8_t*)"Tetris Game with FreeRTOS\r\n");
  
  // Инициализация игры
  Tetris_Init(&tetris_game);
  
  // Инициализация buzzer
  Buzzer_Init();
  Buzzer_Set_Volume(BUZZER_VOLUME_MUTE);
  
  // Создание очереди для клавиатуры
  key_queue = xQueueCreate(10, sizeof(uint8_t));
  if (key_queue == NULL) {
    Error_Handler();
  }
  
  // Создание мьютекса для дисплея
  display_mutex = xSemaphoreCreateMutex();
  if (display_mutex == NULL) {
    Error_Handler();
  }
  
  // Создание мьютекса для игры
  game_mutex = xSemaphoreCreateMutex();
  if (game_mutex == NULL) {
    Error_Handler();
  }
  
  // Создание задач FreeRTOS
  xTaskCreate(vKeyboardTask, "Keyboard", configMINIMAL_STACK_SIZE * 2, NULL, 2, NULL);
  xTaskCreate(vGameTask, "Game", configMINIMAL_STACK_SIZE * 4, NULL, 3, NULL);
  xTaskCreate(vDisplayTask, "Display", configMINIMAL_STACK_SIZE * 4, NULL, 1, NULL);
  
  // Запуск планировщика FreeRTOS
  vTaskStartScheduler();
  /* USER CODE END 2 */

  /* Call init function for freertos objects (in cmsis_os2.c) */
  MX_FREERTOS_Init();

  /* Start scheduler */
  osKernelStart();

  /* We should never get here as control is now taken by the scheduler */

  /* Infinite loop */
  /* USER CODE BEGIN WHILE */
  while (1)
  {
    /* USER CODE END WHILE */

    /* USER CODE BEGIN 3 */
  }
  /* USER CODE END 3 */
}

/**
  * @brief System Clock Configuration
  * @retval None
  */
void SystemClock_Config(void)
{
  RCC_OscInitTypeDef RCC_OscInitStruct = {0};
  RCC_ClkInitTypeDef RCC_ClkInitStruct = {0};

  /** Configure the main internal regulator output voltage
  */
  __HAL_RCC_PWR_CLK_ENABLE();
  __HAL_PWR_VOLTAGESCALING_CONFIG(PWR_REGULATOR_VOLTAGE_SCALE3);

  /** Initializes the RCC Oscillators according to the specified parameters
  * in the RCC_OscInitTypeDef structure.
  */
  RCC_OscInitStruct.OscillatorType = RCC_OSCILLATORTYPE_HSI;
  RCC_OscInitStruct.HSIState = RCC_HSI_ON;
  RCC_OscInitStruct.HSICalibrationValue = RCC_HSICALIBRATION_DEFAULT;
  RCC_OscInitStruct.PLL.PLLState = RCC_PLL_NONE;
  if (HAL_RCC_OscConfig(&RCC_OscInitStruct) != HAL_OK)
  {
    Error_Handler();
  }

  /** Initializes the CPU, AHB and APB buses clocks
  */
  RCC_ClkInitStruct.ClockType = RCC_CLOCKTYPE_HCLK|RCC_CLOCKTYPE_SYSCLK
                              |RCC_CLOCKTYPE_PCLK1|RCC_CLOCKTYPE_PCLK2;
  RCC_ClkInitStruct.SYSCLKSource = RCC_SYSCLKSOURCE_HSI;
  RCC_ClkInitStruct.AHBCLKDivider = RCC_SYSCLK_DIV1;
  RCC_ClkInitStruct.APB1CLKDivider = RCC_HCLK_DIV1;
  RCC_ClkInitStruct.APB2CLKDivider = RCC_HCLK_DIV1;

  if (HAL_RCC_ClockConfig(&RCC_ClkInitStruct, FLASH_LATENCY_0) != HAL_OK)
  {
    Error_Handler();
  }
}

/* USER CODE BEGIN 4 */
uint8_t Get_KeyCode(void)
{
  const uint8_t row_constants[4] = {ROW1, ROW2, ROW3, ROW4};
  for (uint8_t row = 0; row < 4; row++) {
    uint8_t col_code = Check_Row(row_constants[row]);
    if (col_code != 0x00) {
      uint8_t col;
      switch (col_code) { case 0x01: col = 2; break; case 0x02: col = 1; break; case 0x04: col = 0; break; default: return 0xFF; }
      return (uint8_t)(row * 3 + col);
    }
  }
  return 0xFF;
}

// Задача обработки клавиатуры
void vKeyboardTask(void *pvParameters)
{
  uint8_t old_code = 0xFF;
  uint8_t code;
  uint32_t last_send_time = 0;
  
  (void)pvParameters;
  
  while (1) {
    code = Get_KeyCode();
    
    if (code == 0xFF) {
      old_code = 0xFF;
      // Сбросить флаг ускоренного падения
      if (xSemaphoreTake(game_mutex, pdMS_TO_TICKS(10)) == pdTRUE) {
        tetris_game.fast_drop = false;
        xSemaphoreGive(game_mutex);
      }
    } else {
      uint32_t current_time = HAL_GetTick();
      
      if (code != old_code) {
        // Новая кнопка нажата - отправить в очередь
        xQueueSend(key_queue, &code, 0);
        old_code = code;
        last_send_time = current_time;
      } else {
        // Та же кнопка удерживается
        // Для кнопки вниз (код 7) отправляем повторно для ускорения
        if (code == 7 && (current_time - last_send_time) >= 100) {
          xQueueSend(key_queue, &code, 0);
          last_send_time = current_time;
          // Установить флаг ускоренного падения
          if (xSemaphoreTake(game_mutex, pdMS_TO_TICKS(10)) == pdTRUE) {
            tetris_game.fast_drop = true;
            xSemaphoreGive(game_mutex);
          }
        }
      }
    }
    
    vTaskDelay(pdMS_TO_TICKS(20)); // Задержка 20 мс для более быстрой реакции
  }
}

// Задача игровой логики
void vGameTask(void *pvParameters)
{
  uint8_t key_code;
  
  (void)pvParameters;
  
  while (1) {
    // Обработка нажатий клавиш
    if (xQueueReceive(key_queue, &key_code, pdMS_TO_TICKS(10)) == pdTRUE) {
      // Захватить мьютекс для безопасного доступа к игре
      if (xSemaphoreTake(game_mutex, portMAX_DELAY) == pdTRUE) {
        // Маппинг клавиш для управления Тетрисом:
        // Клавиатура 4x3:
        // 7 8 9
        // 4 5 6
        // 1 2 3
        // < 0 >
        // 
        // Управление:
        // 4 - влево
        // 6 - вправо
        // 2 - вниз (быстрое падение, ускорение при удержании)
        // 5 - поворот по часовой стрелке
        // 8 - поворот против часовой стрелки (кнопка 1)
        // 0 - пауза/продолжить
        
        if (!tetris_game.game_over) {
          switch (key_code) {
            case 3: // Кнопка 4 (влево)
              Tetris_MovePiece(&tetris_game, -1, 0);
              break;
            case 5: // Кнопка 6 (вправо)
              Tetris_MovePiece(&tetris_game, 1, 0);
              break;
            case 7: // Кнопка 2 (вниз)
              Tetris_DropPiece(&tetris_game);
              break;
            case 4: // Кнопка 5 (поворот по часовой)
              Tetris_RotatePiece(&tetris_game);
              break;
            case 6: // Кнопка 1 (поворот против часовой)
              Tetris_RotatePieceCounterClockwise(&tetris_game);
              break;
            case 10: // Кнопка 0 (пауза)
              tetris_game.paused = !tetris_game.paused;
              break;
            default:
              break;
          }
        } else {
          // После окончания игры любая клавиша перезапускает игру
          Tetris_Init(&tetris_game);
        }
        
        // Освободить мьютекс
        xSemaphoreGive(game_mutex);
      }
    }
    
    // Обновление игровой логики
    if (xSemaphoreTake(game_mutex, pdMS_TO_TICKS(10)) == pdTRUE) {
      uint32_t lines_cleared_before = tetris_game.lines_cleared;
      Tetris_Update(&tetris_game);
      
      // Если линии были удалены, установить время выключения звука
      if (tetris_game.lines_cleared > lines_cleared_before) {
        // Звук уже включен в Tetris_ClearLines, выключим через 150мс
        sound_off_time = HAL_GetTick() + 150;
      }
      
      xSemaphoreGive(game_mutex);
    }
    
    // Выключить звук если прошло достаточно времени
    uint32_t current_time = HAL_GetTick();
    if (sound_off_time > 0 && current_time >= sound_off_time) {
      Buzzer_Set_Volume(BUZZER_VOLUME_MUTE);
      sound_off_time = 0;
    }
    
    vTaskDelay(pdMS_TO_TICKS(10)); // Небольшая задержка
  }
}

// Задача отрисовки
void vDisplayTask(void *pvParameters)
{
  (void)pvParameters;
  
  while (1) {
    // Захватить мьютексы для безопасного доступа к игре и дисплею
    if (xSemaphoreTake(game_mutex, portMAX_DELAY) == pdTRUE) {
      if (xSemaphoreTake(display_mutex, portMAX_DELAY) == pdTRUE) {
        // Отрисовать игру
        Tetris_Draw(&tetris_game);
        oled_UpdateScreen();
        
        // Освободить мьютексы
        xSemaphoreGive(display_mutex);
      }
      xSemaphoreGive(game_mutex);
    }
    
    vTaskDelay(pdMS_TO_TICKS(50)); // Обновление экрана каждые 50 мс
  }
}
/* USER CODE END 4 */

/**
  * @brief  This function is executed in case of error occurrence.
  * @retval None
  */
void Error_Handler(void)
{
  /* USER CODE BEGIN Error_Handler_Debug */
  /* User can add his own implementation to report the HAL error return state */
  __disable_irq();
  while (1)
  {
  }
  /* USER CODE END Error_Handler_Debug */
}
#ifdef USE_FULL_ASSERT
/**
  * @brief  Reports the name of the source file and the source line number
  *         where the assert_param error has occurred.
  * @param  file: pointer to the source file name
  * @param  line: assert_param error line source number
  * @retval None
  */
void assert_failed(uint8_t *file, uint32_t line)
{
  /* USER CODE BEGIN 6 */
  /* User can add his own implementation to report the file name and line number,
     ex: printf("Wrong parameters value: file %s on line %d\r\n", file, line) */
  /* USER CODE END 6 */
}
#endif /* USE_FULL_ASSERT */
