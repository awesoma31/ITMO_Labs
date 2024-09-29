; print_hex.asm
section .data
nl: db 10

codes:
    db '0123456789ABCDEF'

section .text
global  _start

_start:
    ; number 1122... in hexadecimal format
  mov  rdi, 14
  call print_hex
  
  mov  rdi, 23
  call print_hex
  
  mov  rdi, 1
  call print_hex
  
  mov rax, 60
  xor rdi, rdi
  syscall
    ; Each 4 bits should be output as one hexadecimal digit
    ; Use shift and bitwise AND to isolate them
    ; the result is the offset in 'codes' array
print_nl:
  mov rax, 1
  mov rdi, 1
  mov rsi, nl
  mov rdx, 1
  syscall
  ret
  
print_hex:
  mov rax, rdi
  mov rdi, 1
  mov rdx, 1
  mov rcx, 64
  .loop:
    push rax
    sub  rcx, 4
    ; cl is a register, smallest part of rcx
    ; rax -- eax -- ax -- ah + al
    ; rcx -- ecx -- cx -- ch + cl
    sar  rax, cl
    and  rax, 0xf

    lea rsi, [codes + rax]
    mov rax, 1

    ; syscall leaves rcx and r11 changed
    push rcx
    syscall
    pop  rcx

    pop  rax
    ; test can be used for the fastest 'is it a zero?' check
    ; see docs for 'test' command
    test rcx, rcx
    jnz  .loop

    mov rax, 1
    mov rdi, 1
    mov rsi, nl ; print space
    mov rdx, 1
    syscall

    ret