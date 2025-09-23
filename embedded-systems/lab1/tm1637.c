#include "tm1637.h"
// Функция задержки
void delay_us(uint32_t us) {
  // При 48 MHz, 1 цикл ≈ 20.83 нс
  // Эмпирическая задержка (может потребовать калибровки)
  volatile uint32_t cycles = us * 24;
  while (cycles-- > 0) {
    __asm__("nop");
  }
}

// Инициализация пинов TM1637
void tm1637_init(void) {
  // Включаем тактирование GPIOA (уже есть в вашем коде)
  // RCC->AHBENR |= RCC_AHBENR_GPIOAEN;

  // Настраиваем PA6 и PA7 как выходы с открытым стоком
  GPIOA->MODER = (GPIOA->MODER & ~(0xF << (TM1637_CLK_PIN * 2))) | (0x5 << (TM1637_CLK_PIN * 2));
  GPIOA->OTYPER |= (1 << TM1637_CLK_PIN) | (1 << TM1637_DIO_PIN);
  GPIOA->OSPEEDR |= (0x3 << (TM1637_CLK_PIN * 2)) | (0x3 << (TM1637_DIO_PIN * 2));

  // Устанавливаем высокий уровень на обоих пинах
  GPIOA->BSRR = (1 << TM1637_CLK_PIN) | (1 << TM1637_DIO_PIN);

  // Инициализация дисплея
  tm1637_start();
  tm1637_write_byte(TM1637_CMD1);
  tm1637_stop();

  tm1637_start();
  tm1637_write_byte(TM1637_CMD3);
  tm1637_stop();

  tm1637_clear();
  tm1637_display_number(0);

  last_display_update = 0;
  counter = 0;
}

// Начало передачи
void tm1637_start(void) {
  GPIOA->BSRR = (1 << TM1637_DIO_PIN) | (1 << TM1637_CLK_PIN);  // DIO=1, CLK=1
  delay_us(2);
  GPIOA->BRR = (1 << TM1637_DIO_PIN);  // DIO=0
  delay_us(2);
  GPIOA->BRR = (1 << TM1637_CLK_PIN);  // CLK=0
  delay_us(2);
}

// Конец передачи
void tm1637_stop(void) {
  GPIOA->BRR = (1 << TM1637_CLK_PIN);  // CLK=0
  delay_us(2);
  GPIOA->BRR = (1 << TM1637_DIO_PIN);  // DIO=0
  delay_us(2);
  GPIOA->BSRR = (1 << TM1637_CLK_PIN);  // CLK=1
  delay_us(2);
  GPIOA->BSRR = (1 << TM1637_DIO_PIN);  // DIO=1
  delay_us(2);
}

// Запись байта
void tm1637_write_byte(uint8_t byte) {
  for (uint8_t i = 0; i < 8; i++) {
    GPIOA->BRR = (1 << TM1637_CLK_PIN);  // CLK=0
    delay_us(2);

    if (byte & 0x01) {
      GPIOA->BSRR = (1 << TM1637_DIO_PIN);  // DIO=1
    } else {
      GPIOA->BRR = (1 << TM1637_DIO_PIN);  // DIO=0
    }
    delay_us(2);

    GPIOA->BSRR = (1 << TM1637_CLK_PIN);  // CLK=1
    delay_us(2);

    byte >>= 1;
  }

  // Ожидание подтверждения (ACK)
  GPIOA->BRR = (1 << TM1637_CLK_PIN);  // CLK=0
  GPIOA->BSRR = (1 << TM1637_DIO_PIN);  // DIO=1 (отпускаем линию)
  delay_us(2);
  GPIOA->BSRR = (1 << TM1637_CLK_PIN);  // CLK=1
  delay_us(2);

  // Проверяем ACK (DIO должен быть притянут к 0)
  // Пропускаем проверку для упрощения

  GPIOA->BRR = (1 << TM1637_CLK_PIN);  // CLK=0
  delay_us(2);
}

// Отображение цифры на указанной позиции
void tm1637_display_digit(uint8_t digit, uint8_t data) {
  tm1637_start();
  tm1637_write_byte(TM1637_CMD2 | digit);  // Установка адреса
  tm1637_write_byte(data);                 // Отправка данных
  tm1637_stop();

  // Установка яркости
  tm1637_start();
  tm1637_write_byte(TM1637_CMD3);
  tm1637_stop();
}

// Таблица кодов для 7-сегментного индикатора (0-9, A-F)
const uint8_t digit_codes[] = {
  0x3F, // 0
  0x06, // 1
  0x5B, // 2
  0x4F, // 3
  0x66, // 4
  0x6D, // 5
  0x7D, // 6
  0x07, // 7
  0x7F, // 8
  0x6F, // 9
  0x77, // A
  0x7C, // b
  0x39, // C
  0x5E, // d
  0x79, // E
  0x71  // F
};

// Отображение числа на дисплее
void tm1637_display_number(int number) {
  uint8_t digits[4];

  // Извлекаем отдельные цифры
  digits[0] = digit_codes[number % 10];
  digits[1] = (number >= 10) ? digit_codes[(number / 10) % 10] : 0;
  digits[2] = (number >= 100) ? digit_codes[(number / 100) % 10] : 0;
  digits[3] = (number >= 1000) ? digit_codes[(number / 1000) % 10] : 0;

  // Отправляем цифры на дисплей
  tm1637_start();
  tm1637_write_byte(0x40);  // Автоинкремент адреса
  tm1637_stop();

  tm1637_start();
  tm1637_write_byte(0xC0);  // Начальный адрес

  for (int i = 3; i >= 0; i--) {
    tm1637_write_byte(digits[i]);
  }

  tm1637_stop();

  // Установка яркости
  tm1637_start();
  tm1637_write_byte(0x8F);
  tm1637_stop();
}

// Очистка дисплея
void tm1637_clear(void) {
  for (uint8_t i = 0; i < 4; i++) {
    tm1637_display_digit(i, 0x00);
  }
}

static uint8_t cur_switch = 1;

extern uint8_t read_switches(void);

static void tm1637_display_raw4(uint8_t d0, uint8_t d1, uint8_t d2, uint8_t d3) {
  tm1637_start();
  tm1637_write_byte(TM1637_CMD1);
  tm1637_stop();

  tm1637_start();
  tm1637_write_byte(TM1637_CMD2 | 0x00); 
  tm1637_write_byte(d0);
  tm1637_write_byte(d1);
  tm1637_write_byte(d2);
  tm1637_write_byte(d3);
  tm1637_stop();

  tm1637_start();
  tm1637_write_byte(TM1637_CMD3); 
  tm1637_stop();
}

static void show_dec(uint16_t v) {
  uint8_t d3 = digit_codes[v % 10]; v/=10;
  uint8_t d2 = (v? digit_codes[v % 10] : 0); v/=10;
  uint8_t d1 = (v? digit_codes[v % 10] : 0); v/=10;
  uint8_t d0 = (v? digit_codes[v % 10] : 0);
  tm1637_display_raw4(d0,d1,d2,d3);
}

static void show_hex(uint8_t v) {
  uint8_t hi = digit_codes[(v>>4)&0xF];
  uint8_t lo = digit_codes[v & 0xF];
  tm1637_display_raw4(0,0,hi,lo); // __HL
}

static void show_bin_step(uint8_t idx1_8, uint8_t bit) {
  uint8_t d0 = 0;
  uint8_t d1 = digit_codes[idx1_8 % 10];
  uint8_t d2 = 0;
  uint8_t d3 = digit_codes[bit ? 1 : 0];
  tm1637_display_raw4(d0,d1,d2,d3);
}

void tm1637_update(void) {
  if ((tickCount - last_display_update) >= 500) {
    last_display_update = tickCount;

    uint8_t sw = read_switches();

      switch (Mode) {
      case MODE_B:
        uint8_t bit = (sw >> (cur_switch - 1)) & 1u;
        show_bin_step(cur_switch, bit);
        cur_switch++; if (cur_switch > 8) cur_switch = 1;
        break;
      case MODE_D: 
        show_dec(sw);
        break;
      case MODE_H:
        show_hex(sw);
        break;
      default: break;
      }

    counter++;
    if (counter > 9999) counter = 0;
  }
}