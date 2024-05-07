ORG 0x0
V0: WORD $default, 	0X180
V1: WORD $default,	0X180
V2: WORD $int2,   	0X180 ;0x2
V3: WORD $int3, 	0x180 ;0x3
V4: WORD $default,	0X180
V5: WORD $default,	0X180
V6: WORD $default,	0X180
V7: WORD $default, 	0X180

ORG 0x046
X: WORD ?

max: WORD 0x0019 		; 25, максимальное значение Х
min: WORD 0xFFE6 		; -26, минимальное значение Х

default:	IRET 		; Обработка прерывания по умолчанию

START: 		DI

			cla
			out 0x1 
			out 0x3
			out 0xA
			out 0xD
			out 0x11
			out 0x15
			out 0x19
			out 0x1D
			
			ld #0xA ; (1000|0010) = 1010 = 0xA
			out 0x5
			ld #0xB ; (1000|0011) = 1011 = 0xB
			out 0x7
			
			EI
main: 		DI

			ld X 
			dec
			call check
			st x

			EI
			jump main
		
int2:  	DI
		push

		in 4
		call check
		or $X
		call check
		ld $X
		
		pop
		EI
		IRET

int3:  	DI
		push
		
		ld $X
		add X
		add X
		add X
		add X
		add 6
		
		out 6
		
		pop
		EI
		IRET

check: 

