    .data
SUM_LOW:         .word  0
SUM_HIGH:        .word  0
TEMP:            .word  0
INP_ADDR:        .word  0x80
OUT_ADDR:        .word  0x84
INVALID:         .word  0xFFFFFFFF
OF_OUT:          .word  0xCCCCCCCC
ONE:             .word  1

    .text
_start:
LOOP:
    load_ind     INP_ADDR
    beqz         END
    store_addr   TEMP
    add          SUM_LOW
    store_addr   SUM_LOW

    load_addr    TEMP
    bgt          POSITIVE
    beqz         POSITIVE
    bvc          LOOP
POSITIVE:
    bcc          LOOP
    load_addr    SUM_HIGH
    add          ONE
    bvs          OVERFLOW
    store_addr   SUM_HIGH
    jmp          LOOP

END:
    load_addr    SUM_HIGH
    store_ind    OUT_ADDR
    load_addr    SUM_LOW
    store_ind    OUT_ADDR
    halt

INVALID_INPUT:
    load_ind     INVALID
    store_ind    OUT_ADDR
    halt

OVERFLOW:
    load_ind     OF_OUT
    store_ind    OUT_ADDR
    load_ind     OF_OUT
    store_ind    OUT_ADDR
    halt

