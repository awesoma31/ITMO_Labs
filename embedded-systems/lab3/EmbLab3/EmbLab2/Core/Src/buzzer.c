#include "buzzer.h"
#include "tim.h"

#define BUZZER_TIMER_CLOCK  16000000UL  // HSI = 16 MHz

void Buzzer_Init (void) {
	// Просто подготавливаем таймер, PWM будет запускаться при воспроизведении
}

void Buzzer_Set_Freq (uint16_t freq) {
	if (freq == 0) return;
	
	// Вычисляем ARR и PSC для нужной частоты
	// f = TIMER_CLOCK / ((PSC + 1) * (ARR + 1))
	// Используем фиксированный PSC = 15, тогда ARR = TIMER_CLOCK / (16 * freq) - 1
	uint32_t psc = 15;
	uint32_t arr = (BUZZER_TIMER_CLOCK / ((psc + 1) * freq)) - 1;
	uint32_t ccr = arr / 2;  // 50% duty cycle для максимальной громкости
	
	__HAL_TIM_SET_PRESCALER(&htim2, psc);
	__HAL_TIM_SET_AUTORELOAD(&htim2, arr);
	__HAL_TIM_SET_COMPARE(&htim2, TIM_CHANNEL_1, ccr);
	__HAL_TIM_SET_COUNTER(&htim2, 0);
	HAL_TIM_GenerateEvent(&htim2, TIM_EVENTSOURCE_UPDATE);
}

void Buzzer_Set_Volume (uint16_t volume) {
	// Для простоты: volume > 0 = звук, volume = 0 = тишина
	if (volume > 0) {
		HAL_TIM_PWM_Start(&htim2, TIM_CHANNEL_1);
	} else {
		HAL_TIM_PWM_Stop(&htim2, TIM_CHANNEL_1);
	}
}

void Buzzer_Play (uint32_t* melody, uint32_t* delays, uint16_t len) {
	for(int i = 0; i < len; i++) {
		if (melody[i] != 0) {
			Buzzer_Set_Freq(melody[i]);
			Buzzer_Set_Volume(BUZZER_VOLUME_MAX);
			HAL_Delay(1920 / delays[i]);
			Buzzer_Set_Volume(BUZZER_VOLUME_MUTE);
		} else {
			// REST - просто пауза
			HAL_Delay(1920 / delays[i]);
		}
		HAL_Delay(10);  // небольшая пауза между нотами
	}
}
