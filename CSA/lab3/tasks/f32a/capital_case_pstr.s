\ https://wrench.edu.swampbuds.me/result/2ba1a637-7baa-4243-b613-7c0cc44126d0
    .data
len:             .byte  0
buf:             .byte  '_______________________________'
padding:         .byte  '___'
flag:            .word  1     
input_addr:      .word  0x80
output_addr:     .word  0x84
mask:            .byte  0, '___'
bullshit:        .word  0xCCCCCCCC

    .text

    .org 0x90
_start:
    @p input_addr b!         \ b = input_addr
    lit buf a!               \ a = buf
    capcase

    @p output_addr b!        \ b = output_addr
    lit len a!               \ a = len (длина полученной строки)
    @+ lit 255 and   

    print
    halt

capcase:
    @b lit 255 and dup    
    lit -10 + if end
    dup lit -97 + -if char_ab97

    dup lit -65 + -if char_ab65
    shit_char ;
char_ab65:
    dup lit -90 + -if shit_char
    upcase_char ;
shit_char:
    set_flag_1
    write_char
    capcase ;
char_ab97:
    dup lit -122 + -if shit_char
    downcase_char ;

downcase_char:
    @p flag             
    lit -1 + if do_upcase

    write_char
    capcase ;

upcase_char:
    @p flag
    lit -1 + if write_without_downcase
    do_downcase ;
write_without_downcase:
    write_char
    lit 0 !p flag
    capcase ;

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
    @p len                
    lit 1 +              
    dup                 
    !p len             
    lit 255 and       
    lit -32 + if err 
    ;               

err:
    @p bullshit
    @p output_addr b!
    !b
    halt
end:
    drop
    ;

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

