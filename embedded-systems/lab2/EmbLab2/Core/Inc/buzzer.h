#ifndef INC_BUZZER_H_
#define INC_BUZZER_H_

#include "main.h"

#define N_REST   0U
#define N_A4     440U
#define N_B4     494U
#define N_C5     523U
#define N_D5     587U
#define N_E5     659U
#define N_G5     784U

#define BUZZER_VOLUME_MAX	10
#define BUZZER_VOLUME_MUTE	0

void Buzzer_Init (void);
void Buzzer_Set_Freq (uint16_t freq);
void Buzzer_Set_Volume (uint16_t volume);
void Buzzer_Play (uint32_t* melody, uint32_t* delays, uint16_t len);

#endif /* INC_BUZZER_H_ */
