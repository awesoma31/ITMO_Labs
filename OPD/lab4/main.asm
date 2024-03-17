org 0x453

cla
st R
ld Y
push 
call $f
pop
sub R
st R 
ld Z 
dec
push 
call $f
pop
inc 
sub R 
st R 
ld X
inc
push
call $f
pop
inc
sub R
st R 
hlt

Z : word 0x0
Y: word 0x0
X: word 0x0
R: word 0x0

org 0x732
f:
	ld &1
	bmi dflt
	bzs dflt
	cmp A
	bge dflt
	asl
	asl
	sub &1
	add B
	jump fini
	
dflt: 
	ld A
fini: 
	st &1
	ret
	
A: word 0x0D0D
B: word 0x006D
	