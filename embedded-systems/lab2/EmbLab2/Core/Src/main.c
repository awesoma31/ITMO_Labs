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
#include "main.h"
#include "i2c.h"
#include "tim.h"
#include "usart.h"
#include "gpio.h"

/* USER CODE BEGIN Includes */
#include <stdio.h>
#include <string.h>
#include <inttypes.h>
#include "oled.h"
#include "sdk_uart.h"
#include "kb.h"
#include "buzzer.h"
/* USER CODE END Includes */

/* USER CODE BEGIN PD */
#define TIMER_INPUT_CLOCK_HZ 16000000UL
#define HELP_DISPLAY_TIME_MS 5000  /* Время показа справки в миллисекундах */
/* USER CODE END PD */

/* USER CODE BEGIN PV */
static uint32_t t_hours = 0, t_mins = 0, t_secs = 3;
static uint8_t  timer_running = 0;
static uint32_t last_tick_ms = 0;
static uint8_t  already_rang = 0;
static uint8_t  show_help = 0;      /* Флаг показа справки */
static uint32_t help_start_time = 0; /* Время начала показа справки */

static uint32_t end_melody[] = { N_C5, N_E5, N_G5, N_C5, N_REST, N_G5, N_E5, N_C5 };
static uint32_t end_delays[] = { 8, 8, 8, 8, 8, 8, 8, 4 };
/* USER CODE END PV */

void SystemClock_Config(void);
/* USER CODE BEGIN PFP */
uint8_t Get_KeyCode(void);
static void Draw_Screen(uint8_t selection);
static void Draw_Help_Screen(void);  /* Новая функция для отрисовки справки */
static void Tick_Timer_1Hz(void);
static uint8_t Is_Zero(void);
static void Play_End_Melody(void);
static void Play_484Hz_Tone(void);
static void Show_Help(void);         /* Функция для активации показа справки */
/* USER CODE END PFP */

/* USER CODE BEGIN 0 */
static void format_hhmmss(char *buf, size_t n, uint32_t H, uint32_t M, uint32_t S)
{
  snprintf(buf, n, "%02lu:%02lu:%02lu", (unsigned long)H, (unsigned long)M, (unsigned long)S);
}

static void inc_field(uint8_t field, uint8_t digit)
{
  switch (field)
  {
    case 0: t_hours = (t_hours > 99) ? 0 : (t_hours*10U + digit); if (t_hours > 99) t_hours = 99; break;
    case 1: t_mins  = (t_mins  > 59) ? 0 : (t_mins *10U + digit); if (t_mins  > 59) t_mins  = 59; break;
    case 2: t_secs  = (t_secs  > 59) ? 0 : (t_secs *10U + digit); if (t_secs  > 59) t_secs  = 59; break; /* ОШИБКА: было _tsecs вместо t_secs */
  }
}

static void normalize_time(void)
{
  if (t_secs > 59) t_secs = 59;
  if (t_mins > 59) t_mins = 59;
  if (t_hours > 99) t_hours = 99;
}

static void start_timer(void)
{
  normalize_time();
  timer_running = 1;
  already_rang = 0;
  last_tick_ms = HAL_GetTick();
}

static void stop_timer(void)
{
  timer_running = 0;
}

/* Функция для активации показа справки */
static void Show_Help(void)
{
  show_help = 1;
  help_start_time = HAL_GetTick();
  UART_Transmit((uint8_t*)"HELP: Displaying help screen\r\n");
}
/* USER CODE END 0 */

int main(void)
{
  HAL_Init();
  SystemClock_Config();

  MX_GPIO_Init();
  MX_I2C1_Init();
  MX_USART6_UART_Init();
  MX_TIM2_Init();

  /* USER CODE BEGIN 2 */
  oled_Init();
  UART_Transmit((uint8_t*)"Timer UI + Buzzer on TIM2\r\n");
  Buzzer_Init();
  Buzzer_Set_Volume(BUZZER_VOLUME_MUTE);
  /* USER CODE END 2 */

  enum {SEL_HH, SEL_MM, SEL_SS, SEL_RUN} selection = SEL_HH;

  while (1)
  {
    static const uint8_t keymap[12] = {
      '7','8','9',
      '4','5','6',
      '1','2','3',
      '<','0','>'
    };
    static uint8_t old_code = 0xFF;
    uint8_t code = Get_KeyCode();

    if (code == 0xFF) {
      old_code = 0xFF;
    }
    else if (code != old_code)
    {
      old_code = code;

      /* Проверяем, не нажата ли кнопка справки (кнопка 7 - код 0) */
      if (code == 0 && !timer_running) {
        Show_Help();
        continue;  /* Пропускаем остальную обработку, сразу показываем справку */
      }

      /* Если показывается справка - любая кнопка закрывает её */
      if (show_help) {
        show_help = 0;
        continue;
      }

      /* Обработка остальных кнопок (только если не показывается справка) */
      if (code == 9) {
        if (selection == SEL_HH) selection = SEL_RUN; else selection--;
      }
      else if (code == 11) {
        selection = (selection + 1) % 4;
      }
      else
      {
        if (selection == SEL_RUN) {
          if (code == 10) { start_timer(); UART_Transmit((uint8_t*)"TIMER: START\r\n"); }
          else if (code == 6) { stop_timer(); UART_Transmit((uint8_t*)"TIMER: STOP\r\n"); }
          else if (code == 8) { t_hours = t_mins = t_secs = 0; UART_Transmit((uint8_t*)"TIMER: CLEAR\r\n"); }
        } else if (code <= 10) {
          uint8_t digit = (uint8_t)(keymap[code] - '0');
          inc_field(selection, digit);
          normalize_time();
        }
      }
    }

    /* Проверяем, не истекло ли время показа справки */
    if (show_help && (HAL_GetTick() - help_start_time >= HELP_DISPLAY_TIME_MS)) {
      show_help = 0;
    }

    if (timer_running && !show_help) {
      Tick_Timer_1Hz();
      if (Is_Zero() && !already_rang) {
        already_rang = 1;
        stop_timer();
        Play_End_Melody();
        Play_484Hz_Tone();
      }
    }

    /* Отрисовка экрана */
    if (show_help) {
      Draw_Help_Screen();
    } else {
      Draw_Screen(selection);
    }

    oled_UpdateScreen();

    /* Небольшая задержка для уменьшения нагрузки на процессор */
    HAL_Delay(10);
  }
}

/**
  * @brief System Clock Configuration
  * @retval None
  */
void SystemClock_Config(void)
{
  RCC_OscInitTypeDef RCC_OscInitStruct = {0};
  RCC_ClkInitTypeDef RCC_ClkInitStruct = {0};

  __HAL_RCC_PWR_CLK_ENABLE();
  __HAL_PWR_VOLTAGESCALING_CONFIG(PWR_REGULATOR_VOLTAGE_SCALE3);

  RCC_OscInitStruct.OscillatorType = RCC_OSCILLATORTYPE_HSI;
  RCC_OscInitStruct.HSIState = RCC_HSI_ON;
  RCC_OscInitStruct.HSICalibrationValue = RCC_HSICALIBRATION_DEFAULT;
  RCC_OscInitStruct.PLL.PLLState = RCC_PLL_NONE;
  if (HAL_RCC_OscConfig(&RCC_OscInitStruct) != HAL_OK) { Error_Handler(); }

  RCC_ClkInitStruct.ClockType = RCC_CLOCKTYPE_HCLK|RCC_CLOCKTYPE_SYSCLK
                              |RCC_CLOCKTYPE_PCLK1|RCC_CLOCKTYPE_PCLK2;
  RCC_ClkInitStruct.SYSCLKSource   = RCC_SYSCLKSOURCE_HSI;
  RCC_ClkInitStruct.AHBCLKDivider  = RCC_SYSCLK_DIV1;
  RCC_ClkInitStruct.APB1CLKDivider = RCC_HCLK_DIV1;
  RCC_ClkInitStruct.APB2CLKDivider = RCC_HCLK_DIV1;
  if (HAL_RCC_ClockConfig(&RCC_ClkInitStruct, FLASH_LATENCY_0) != HAL_OK) { Error_Handler(); }
}

/* USER CODE BEGIN 4 */
/* Функция для отрисовки экрана справки */
static void Draw_Help_Screen(void)
{
  oled_Fill(Black);

  /* Заголовок - используем существующий шрифт Font_7x10 */
  oled_SetCursor(0, 0);
  oled_WriteString("=== help ===", Font_7x10, White);

  /* Основные команды */
  oled_SetCursor(0, 12);
  oled_WriteString("< >: navigation", Font_7x10, White);

  oled_SetCursor(0, 24);
  oled_WriteString("0-9: nums", Font_7x10, White);

  oled_SetCursor(0, 36);
  oled_WriteString("in RUN:", Font_7x10, White);

  oled_SetCursor(0, 48);
  oled_WriteString("0=start 6=stop", Font_7x10, White);

  oled_SetCursor(0, 60);
  oled_WriteString("8=reset", Font_7x10, White);
}

static void Draw_Screen(uint8_t selection)
{
  char buf[16];
  oled_Fill(Black);

  oled_SetCursor(0, 0);
  oled_WriteString("HH:", Font_7x10, White);
  snprintf(buf, sizeof(buf), "%02lu", (unsigned long)t_hours);
  oled_WriteString(buf, Font_7x10, White);

  oled_SetCursor(0, 12);
  oled_WriteString("MM:", Font_7x10, White);
  snprintf(buf, sizeof(buf), "%02lu", (unsigned long)t_mins);
  oled_WriteString(buf, Font_7x10, White);

  oled_SetCursor(0, 24);
  oled_WriteString("SS:", Font_7x10, White);
  snprintf(buf, sizeof(buf), "%02lu", (unsigned long)t_secs);
  oled_WriteString(buf, Font_7x10, White);

  switch (selection) {
    case 0: oled_SetCursor(58, 0);  break;
    case 1: oled_SetCursor(58, 12); break;
    case 2: oled_SetCursor(58, 24); break;
    case 3: oled_SetCursor(58, 36); break;
  }
  oled_WriteChar('<', Font_7x10, White);

  oled_SetCursor(0, 36);
  oled_WriteString("RUN: ", Font_7x10, White);
  oled_WriteString(timer_running ? "ON" : "OFF", Font_7x10, White);

  char tline[16];
  format_hhmmss(tline, sizeof(tline), t_hours, t_mins, t_secs);
  oled_SetCursor(0, 48);
  oled_WriteString(tline, Font_7x10, White);

  /* Индикация справки внизу экрана */
  oled_SetCursor(0, 60);
  oled_WriteString("7=Справка", Font_7x10, White); /* Изменено на Font_7x10 */
}

static void Tick_Timer_1Hz(void)
{
  uint32_t now = HAL_GetTick();
  if ((now - last_tick_ms) >= 1000U) {
    last_tick_ms += 1000U;
    if (!Is_Zero()) {
      if (t_secs > 0) t_secs--;
      else {
        t_secs = 59;
        if (t_mins > 0) t_mins--;
        else {
          t_mins = 59;
          if (t_hours > 0) t_hours--;
        }
      }
    }
  }
}

static uint8_t Is_Zero(void)
{
  return (t_hours == 0 && t_mins == 0 && t_secs == 0);
}

static void Play_End_Melody(void)
{
  Buzzer_Play(end_melody, end_delays, sizeof(end_melody)/sizeof(end_melody[0]));
}

static void Play_484Hz_Tone(void)
{
  Buzzer_Set_Freq(484);
  Buzzer_Set_Volume(BUZZER_VOLUME_MAX);
  HAL_Delay(800);
  Buzzer_Set_Volume(BUZZER_VOLUME_MUTE);
}
/* -------	----------------------------- */

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
/* USER CODE END 4 */

void Error_Handler(void)
{
  __disable_irq();
  while (1) { }
}
