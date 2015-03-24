abort_str: .asciiz "function not implemented, aborting!"

__mips_print:
    # $a0 is a pointer to a string
    li $v0, 4
    syscall
    jr $ra

__mips_flush:
    # this isn't something that SPIM lets us do.
    jr $ra

__mips_getchar:
    li $v0, 4
    la $a0, abort_str
    syscall
    jal __mips_exit

__mips_ord:
    li $v0, 4
    la $a0, abort_str
    syscall
    jal __mips_exit

__mips_chr:
    li $v0, 4
    la $a0, abort_str
    syscall
    jal __mips_exit

__mips_size:
    # a0 is a pointer to a null terminated string
    xor $t0, $t0, $t0
_while_begin:
    add $t2, $t0, $a0
    lb $t1 0($t2)
    beq $t1, $zero, _size_done
    addi $t2, $t2, 1
    j _while_begin
_size_done:
    jr $ra

__mips_substring:
    li $v0, 4
    la $a0, abort_str
    syscall
    jal __mips_exit

__mips_concat:
    li $v0, 4
    la $a0, abort_str
    syscall
    jal __mips_exit

__mips_not:
    # $a0 is an integer
    not $v0, $a0
    jr $ra

__mips_exit:
    # $a0 is an int exit code
    move $v0, $a0
    syscall
    # no return

__mips_strcmp:
    li $v0, 4
    la $a0, abort_str
    syscall
    jal __mips_exit

__mips_malloc:
    # $a0 is a number of bytes to allocate
    li $v0, 9
    syscall
    # $v0 now contains a pointer to the allocated memory
    jr $ra

__mips_memset:
    # $a0 is a pointer to an array,
    # $a1 is a number of elements to set,
    # $a2 is the integer value to set them to
    xor $t0, $t0, $t0
_loop_begin:
    bge $t0, $a1, _loop_done
    add $t1, $a0, $t0
    sw $a2, 0($t1)
    add $t0, $t0, 1
    j _loop_begin
_loop_done:
    jr $ra

