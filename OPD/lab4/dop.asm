a: word 0x1
b: word 0x5

push b 
push a

call times


org 0x100
times:
	sum:
		
		loop &2
		jump sum
		
		
	res: word 0x000