org 0x0

cla
ld Y ;2
push
ld X ;1
push
call $multiply
pop
st res
pop
hlt

;-2=0xFFFE
;-6 = 0xFFFA

X: word 0xFFFE ; 
Y: word 0xFFFE ;

res: word 0x0

org 0x300
multiply:
	tmp: word 0x0
	  ld &2 ; y
	  bpl fini
	invert_a_b:
	  neg    ; invert y
	  st &2  ;
	  
	  ld &1  ;
	  neg    ; invert x
	  st &1  ;
	  	  	
	fini: 
	  cla			
	iter: 
	  add &1
      loop &2 
      jump iter
      st &1
      ret
