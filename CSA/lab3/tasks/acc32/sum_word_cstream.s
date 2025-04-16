; sum+word_cstream
; https://wrench.edu.swampbuds.me/result/c65357c8-535f-4ab1-b685-0a13c424dbb5
    .data
SUM_LOW:         .word  0
SUM_HIGH:        .word  0
TEMP:            .word  0
ONE:             .word  1

    .text
.org 0x90
_start:
LOOP:
    load_addr    0x80                      
    beqz         END                      
    store_addr TEMP
    add          SUM_LOW               
    store_addr   SUM_LOW

    load_addr TEMP
    bgt POSITIVE
    beqz POSITIVE
    bvc LOOP
POSITIVE:
    bcc          LOOP
    load_addr    SUM_HIGH
    add          ONE
    bvs          OVERFLOW           
    store_addr   SUM_HIGH
    jmp LOOP

END:
    load_addr    SUM_HIGH
    store_addr   0x84                      
    load_addr    SUM_LOW
    store_addr   0x84                     
    halt

INVALID_INPUT:
    load_imm     0xFFFFFFFF              
    store_addr   0x84                   
    halt

OVERFLOW:
    load_imm     0xCCCCCCCC            
    store_addr   0x84                 
    load_imm     0xCCCCCCCC
    store_addr   0x84                
    halt

