org 	0x04C8  
A: 		word 	0x02 		; 04c8
B: 		word 	0x03 		; 04c9
X: 		word 	0x03 		; 04ca
Y: 		word 	0x02 		; 04cb
res1: 	word 	0x0 		; 04cc
res2: 	word 	0x0 		; 04cd

START:	

test1: 	ld 		$A 
		word 	0x94C9 	; B - A -> B
		bmi 	err1
		ld 		$B
		bmi 	err1
		cmp 	#1
		bne 	err1
		ld 		#1
		st 		res1 

test2:	ld 		$X
		word 	0x94CB 	; Y - X -> Y
		bpl 	err1
		ld 		$Y
		bpl 	err2
		
		cmp 	M
		bne 	err2
		ld 		#1
		st 		res2
		hlt

err1: 	jump 	test2

err2: 	cla
		st 		res2
		hlt
		
M: 		word 	0xFFFF
END
