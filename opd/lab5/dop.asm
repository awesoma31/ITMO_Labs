; организовать вывод на ВУ-7

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
		st tmp
		
		cmp fin 	; проверка на стоп-символ
		beq exit
		;cla
		
out1:	in 0x15
		and mask
		beq out1
		
		ld tmp
		out 0x14
		
		jump s1

exit:	hlt	
	