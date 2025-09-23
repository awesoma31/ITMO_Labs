#include "main.h"
#include "tm1637.h"
#include "keyboard.h"

volatile uint32_t tickCount;
uint32_t last_display_update;
uint16_t counter;
char lastKey;
uint32_t lastScanTime;

void osSystickHandler(void) {
  tickCount++;
}

void initGPIO() {
  // Включаем тактирование GPIOA и GPIOB
  RCC->AHBENR |= RCC_AHBENR_GPIOAEN | RCC_AHBENR_GPIOBEN |  RCC_AHBENR_GPIOCEN;

  // Настраиваем PA5 как выход
  GPIOA->MODER = (GPIOA->MODER & ~(3 << 10)) | (1 << 10);
  GPIOA->OTYPER &= ~(1 << 5);
  GPIOA->OSPEEDR |= (1 << 10);

  // led
  //pc7
  GPIOC->MODER   = (GPIOC->MODER & ~(3 << (7*2))) | (1 << (7*2));
  GPIOC->OTYPER &= ~(1 << 7);
  GPIOC->OSPEEDR = (GPIOC->OSPEEDR & ~(3 << (7*2))) | (1 << (7*2));

  //pa9
  GPIOA->MODER   = (GPIOA->MODER & ~(3 << (9*2)))  | (1 << (9*2));
  GPIOA->OTYPER &= ~(1 << 9);
  GPIOA->OSPEEDR = (GPIOA->OSPEEDR & ~(3 << (9*2))) | (1 << (9*2));
  GPIOA->PUPDR  &= ~(3 << (9*2));

  // PA15
  GPIOA->MODER   = (GPIOA->MODER & ~(3 << (15*2))) | (1 << (15*2));
  GPIOA->OTYPER &= ~(1 << 15);
  GPIOA->OSPEEDR = (GPIOA->OSPEEDR & ~(3 << (15*2))) | (1 << (15*2));
  GPIOA->PUPDR  &= ~(3 << (15*2));

  // switches
  // PA12, PA11, PA4, PA1, PA0 как входы
  GPIOA->MODER &= ~((3<<(12*2)) | (3<<(11*2)) | (3<<(4*2)) |
                    (3<<(1*2))  | (3<<(0*2)));

  // PB1 как вход
  GPIOB->MODER &= ~(3<<(1*2));

  // PC14, PC13 как входы
  GPIOC->MODER &= ~((3<<(14*2)) | (3<<(13*2)));

  // входы
  GPIOA->MODER &= ~((3<<(12*2))|(3<<(11*2))|(3<<(4*2))|(3<<(1*2))|(3<<(0*2)));
  GPIOB->MODER &= ~(3<<(1*2));
  GPIOC->MODER &= ~((3<<(14*2))|(3<<(13*2)));

  GPIOA->PUPDR = (GPIOA->PUPDR & ~((3<<(12*2))|(3<<(11*2))|(3<<(4*2))|(3<<(1*2))|(3<<(0*2))))
              |  (2<<(12*2)) | (2<<(11*2)) | (2<<(4*2)) | (2<<(1*2)) | (2<<(0*2));
  GPIOB->PUPDR = (GPIOB->PUPDR & ~(3<<(1*2))) | (2<<(1*2));
  GPIOC->PUPDR = (GPIOC->PUPDR & ~((3<<(14*2))|(3<<(13*2)))) | (2<<(14*2)) | (2<<(13*2));
}

void initUSART2() {
  // Включаем тактирование USART2
  RCC->APB1ENR |= RCC_APB1ENR_USART2EN;

  // Настраиваем PA2 и PA3 в альтернативный режим
  GPIOA->MODER = (GPIOA->MODER & ~(0xF << 4)) | (0xA << 4);
  GPIOA->AFR[0] = (GPIOA->AFR[0] & ~(0xFF << 8)) | (1 << 8) | (1 << 12);

  // Настраиваем USART2
  USART2->BRR = 417; // 48MHz/115200
  USART2->CR1 = USART_CR1_TE | USART_CR1_UE;
}

void initSysTick() {
  SysTick->LOAD = 47999; // 1ms при 48MHz
  SysTick->VAL = 0;
  SysTick->CTRL = (1 << 2) | (1 << 1) | (1 << 0);
}

int _write(int file, uint8_t *ptr, int len) {
  for (int i = 0; i < len; i++) {
    while (!(USART2->ISR & USART_ISR_TXE));
    USART2->TDR = ptr[i];
  }
  return len;
}

static inline void pin_on(GPIO_TypeDef *P, uint8_t pin)  { P->BSRR = (1u << pin); }
static inline void pin_off(GPIO_TypeDef *P, uint8_t pin) { P->BSRR = (1u << (pin + 16)); }

static void leds_apply_mode(Mode_t m) {
  pin_off(GPIOA, 9);
  pin_off(GPIOA, 15);
  pin_off(GPIOC, 7);

  switch (m) {
    case MODE_B: pin_on(GPIOA, 15); break; // BIN -> PA15
    case MODE_D: pin_on(GPIOA, 9);  break; // DEC -> PA9
    case MODE_H: pin_on(GPIOC, 7);  break; // HEX -> PC7
    default: break;
  }
}

uint8_t read_switches(void) {
    uint8_t val = 0;

    val |= ((GPIOA->IDR >> 12) & 1) << 0; // SW1 -> bit0
    val |= ((GPIOA->IDR >> 11) & 1) << 1; // SW2 -> bit1
    val |= ((GPIOB->IDR >>  1) & 1) << 2; // SW3 -> bit2
    val |= ((GPIOA->IDR >>  4) & 1) << 3; // SW4 -> bit3
    val |= ((GPIOA->IDR >>  1) & 1) << 4; // SW5 -> bit4
    val |= ((GPIOA->IDR >>  0) & 1) << 5; // SW6 -> bit5
    val |= ((GPIOC->IDR >> 14) & 1) << 6; // SW7 -> bit6
    val |= ((GPIOC->IDR >> 13) & 1) << 7; // SW8 -> bit7

    return val;
}

typedef enum { SW1=0, SW2, SW3, SW4, SW5, SW6, SW7, SW8 } SwitchId;

static inline uint8_t gpio_read(GPIO_TypeDef *P, uint8_t pin) {
    return (uint8_t)((P->IDR >> pin) & 1u);
}

static GPIO_TypeDef* const SW_PORT[8] = { GPIOA,GPIOA,GPIOB,GPIOA,GPIOA,GPIOA,GPIOC,GPIOC };
static const uint8_t       SW_PIN [8] = {     12,   11,   1,   4,   1,   0,  14,  13 };

uint8_t read_switch(SwitchId sw) {
    return gpio_read(SW_PORT[sw], SW_PIN[sw]);
}



void checkTickCount() {
  if ((tickCount % 1000) == 0) {
    GPIOA->ODR ^= (1 << 5); // Toggle LED
  }
}

int main(void) {
  initGPIO();
  initUSART2();
  initSysTick();
  initKeyboard();
  tm1637_init();

  while (1) {
    checkTickCount();
    leds_apply_mode(Mode);
    tm1637_update();
    scanKeyboard();
  }

  return 0;
}