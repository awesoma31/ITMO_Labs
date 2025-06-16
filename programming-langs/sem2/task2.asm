section .text

; Принимает указатель на нуль-терминированную строку, возвращает её длину
string_length:
    xor rax, rax
    mov rax, rdi
    .counter:
        cmp byte[rdi], 0
        jz  .end
        inc rdi
        jmp .counter
    .end:
        sub rdi, rax
        mov rax, rdi
        xor rdi, rdi
        xor rsi, rsi
        ret

; Принимает указатель на нуль-терминированную строку, выводит её в stdout
print_string:
    mov  rsi, rdi
    push rsi
    call string_length
    pop  rsi
    mov  rdx, rax
    mov  rax, 1
    mov  rdi, 1
    syscall
    ret