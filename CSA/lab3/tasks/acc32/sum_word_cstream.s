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
    store        TEMP
    add          SUM_LOW
    store        SUM_LOW

    load         TEMP
    bgt          POSITIVE
    bvc          LOOP
POSITIVE:
    bcc          LOOP
    load         SUM_HIGH
    add          ONE
    bvs          OVERFLOW
    store        SUM_HIGH
    jmp          LOOP

END:
    load         SUM_HIGH
    store_ind    OUT_ADDR
    load         SUM_LOW
    store_ind    OUT_ADDR
    halt

INVALID_INPUT:
    load         INVALID
    store_ind    OUT_ADDR
    halt

OVERFLOW:
    load         OF_OUT
    store_ind    OUT_ADDR
    load         OF_OUT
    store_ind    OUT_ADDR
    halt

