    .data
buf:             .byte  0, '_______________________________'
padding:         .byte  '___'
flag:            .word  1
input_addr:      .word  0x80
output_addr:     .word  0x84
mask:            .byte  0, '___'
bad_output:      .word  0xCCCCCCCC

    .text
    .org 0x90
_start:
    @p input_addr b!         \ b = input_addr
    lit buf lit 1 + a!       \ a = buf+1
    capcase

    @p output_addr b!        \ b = output_addr
    lit buf a!               \ a = buf 
    @+ lit 255 and

    print
    halt

capcase:
    @b lit 255 and dup
    lit -10 + if end
    dup lit -97 + -if char_ab97

    dup lit -65 + -if char_ab65
    bad_char ;
end:
    drop
    ;

char_ab97:
    dup lit -122 + -if bad_char
    downcase_char ;
char_ab65:
    dup lit -90 + -if bad_char
    upcase_char ;
bad_char:
    set_flag_1
    write_char
    capcase ;

downcase_char:
    @p flag
    lit -1 + if do_upcase
    write_char
    capcase ;
upcase_char:
    @p flag
    lit -1 + if write_without_downcase
    do_downcase ;

do_upcase:
    dup lit -32 +
    write_char
    lit 0 !p flag
    capcase ;

do_downcase:
    dup lit 32 +
    write_char
    lit 0 !p flag
    capcase ;

write_char:
    @p mask +
    !+
    @p buf lit 1 +
    dup       
    !p buf   
    lit 255 and
    lit -32 + if err
    ;
write_without_downcase:
    write_char
    lit 0 !p flag
    capcase ;

err:
    @p bad_output
    @p output_addr b!
    !b
    halt

print:
    dup if end_print
    lit -1 +
    @+ lit 255 and
    !b
    print ;
end_print:
    drop
    ;

set_flag_1:
    lit 1 !p flag
    ;

