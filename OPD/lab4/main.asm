org 0x453

cla ; 0 -> AC
st R ; 0 -> R
ld Y ; Y -> AC
push 
call $f ; f(y)
pop 
sub R ; f(y) - 0
st R ; f(y) - 0 -> R
ld Z ; Z -> AC
dec ; Z-1
push 
call $f ; f(z-1)
pop
inc ; f(z-1)+1
sub R ; f(z-1)+1 - ( f(y) )
st R ; f(z-1)+1 - ( f(y) ) -> R
ld X ; X -> AC
inc ; X+1
push
call $f ; f(x+1)
pop
inc ; f(x+1)+1
sub R ; f(x+1)+1 - ( f(z-1)+1 - ( f(y) ) ) = f(x+1)-f(z-1)+f(y)
st R ; f(x+1)-f(z-1)+f(y) -> R
hlt

Z : word 0x1
Y: word 0x0
X: word 0xFFF
R: word 0x0070



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
	