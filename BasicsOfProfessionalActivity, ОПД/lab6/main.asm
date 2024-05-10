ORG 		0x0
V0: 		WORD $default, 	0X180
V1: 		WORD $default,	0X180
V2: 		WORD $int2,   	0X180 
V3: 		WORD $int3, 	0x180 	
V4: 		WORD $default,	0X180
V5: 		WORD $default,	0X180
V6: 		WORD $default,	0X180
V7: 		WORD $default, 	0X180

ORG 		0x046
X: 			WORD 0x0018 			; 24

max: 		WORD 0x0018 			; 24, максимальное значение Х
min: 		WORD 0xFFE7 			; -25, минимальное значение Х

default:	IRET 					; Обработка прерывания по умолчанию

START: 		DI

			cla
			out 0x1 
			out 0x3
			out 0xA
			out 0xD
			out 0x11 				; запрет прерываний для неиспользуемых ВУ
			out 0x15
			out 0x19
			out 0x1D
			
			ld #0xA 				; (1000|0010) = 1010 = 0xA
			out 0x5
			ld #0xB 				; (1000|0011) = 1011 = 0xB
			out 0x7
			
			EI
main: 		DI

			ld $X 
			dec
			call check
			st $X

			EI
			jump main
		
int2:  		DI
			push

			in 0x4
			or $X
			call check
			ld $X
		
			pop
			EI
			IRET

int3:  		DI
			push
		
			ld $X
			add $X
			add $X
			add $X
			add $X
			add #6
			call check
		
			out 0x6
		
			pop
			EI
			IRET

check: 	
check_min: 	cmp min
			bpl check_max
			jump ld_max
check_max:	cmp max
			bmi fini
		
ld_max: 	ld $max
fini: 		ret

