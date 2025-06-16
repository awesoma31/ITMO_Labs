    .data
input_addr:      .word  0x80
output_addr:     .word  0x84
stack_start:       .word  0x400

    .text
_start:
    lui      t0, %hi(input_addr)
    addi     t0, t0, %lo(input_addr)
    lw       t1, 0(t0)                       ; t1 = 0x80
    lw       a0, 0(t1)                       ; a0 = inp val

    lui      a1, %hi(stack_start)
    addi     a1, sp, %lo(stack_start)
    lw       sp, 0(a1)

    addi     sp, sp, -4
    addi     t4, zero, 4                     ; store 4 in t4  (how many times to loop)
    sw       t4, 0(sp)                       

    jal      ra, big_to_little_endian

    addi     sp, sp, 4

    lui      t0, %hi(output_addr)
    addi     t0, t0, %lo(output_addr)
    lw       t1, 0(t0)
    sw       a0, 0(t1)

    halt
    
    .text
    .org 0x90
big_to_little_endian:
    addi     t5, zero, 0xff

    mv       t0, a0                          ; t0 = original value
    addi     a0, zero, 0                     ; initialize result to 0
    addi     t1, zero, 0                     ; initialize loop counter

    lw       t2, 0(sp)

byte_swap_loop:
    beq      t1, t2, byte_swap_done          ; exit loop when counter reaches 4

    and      t3, t0, t5                      ; extract current byte

    addi     t6, zero, 3
    sub      t6, t6, t1                      ; calculate shift amount (3-i)*8
    addi     t4, zero, 8
    mul      t6, t6, t4                      ; t6 = (3-i)*8

    sll      t3, t3, t6                      ; shift byte to its new position
    or       a0, a0, t3                      ; add byte to result

    addi     t6, zero, 8
    srl      t0, t0, t6                      ; shift original value right by 8 bits
    addi     t1, t1, 1                       ; increment counter
    j        byte_swap_loop

byte_swap_done:
    jr       ra

