org 0x0

cla
ld Y
push
ld X
push
call $divide
pop
st R
pop
hlt

; x // y

X: word -8 ;1
Y: word 2 ;2
R: word 0

org 0x300
RES: word 0
divide:
		ld &1
		bpl x_pos
	x_neg:
		ld &2
		bpl neg_pos_invert
		jump neg_neg_invert
	x_pos:
		ld &2
		bpl pos_pos_iter
		jump pos_neg_iter
	neg_neg_invert:
		ld &1
		neg
		st &1
		ld &2
		neg
		st &2
		jump pos_pos_iter
		
	neg_pos_invert:
		ld &1
		neg
		st &1
		ld &2
		neg
		st &2
		jump pos_neg_iter
		
	pos_neg_iter:
		ld &1
		bmi pn_fini
		add &2
		st &1
		cmp -(RES)
		jump pos_neg_iter
		
	pn_fini:
		ld RES
		inc
		st &1
		ret
		
    pos_pos_iter:
		ld &1
		bmi pp_fini
		sub &2
		st &1
		cmp (RES)+
		jump pos_pos_iter
	pp_fini:
	    ld RES
	    dec
	    st &1
	    ret