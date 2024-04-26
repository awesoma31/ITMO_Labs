org 	0x1CF

mask: 	word 0x40 	
fin:	word 0x0A 	; стоп-символ
res: 	word 0x646 	; ссылка на результат
tmp:	word ? 

START:	cla
s1:		in 5
		and mask 	; ожидание ввода символа
		beq s1
	
		in 4 		; ввод байта в AC
		st (res)
		st tmp
		cmp fin 	; проверка на стоп-символ
		beq exit
		cla	

s2: 	in 5
		and mask 	; ожидание ввода символа
		beq s2
	
		in 4 		; символ в AС
		swab 		; перемещаем в старший байт
		or tmp 		; совмещаем с первым символом
		st (res) 	; сохраняем в память по ссылке
		sub tmp
		swab 		; перемещаем в младший байт
		cmp fin 	; проверяем на стоп-символ
		beq exit
		ld (res)+
		cla 
		jump s1
	
exit:	ld (res)+	
		hlt	
	