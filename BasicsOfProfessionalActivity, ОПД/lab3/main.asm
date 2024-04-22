ORG 0x265

ARR_START: word 0x0275
ARR_END: word 0xA000
ARR_LEN: word 0xE000
C: word 0xE000

start:
cla
st C
ld #3
st ARR_LEN
add ARR_START
st ARR_END
begin_loop: ld -(ARR_END)
beq iter
cmp (C)+
iter: loop $ARR_LEN
jump begin_loop
hlt

ARRAY: word 0x0000, 0x0000, 0x0200