def create_fib_nums(length):
    fib_nums = [1]
    fib1 = fib2 = 1
    for i in range(1, length):
        fib1, fib2 = fib2, fib1 + fib2
        fib_nums.append(fib2)

    return fib_nums


def fib_to_dec(fib_n):
    bin_init_num = bin(fib_n)[2:]
    fib_nums = create_fib_nums(len(bin_init_num))
    num_in_fib = 0
    for i in range(len(bin_init_num)):
        if int(bin_init_num[i]) == 1:
            num_in_fib += fib_nums[i]

    return num_in_fib


a = int(input('Введите число в СС Фибоначчи\n'))
print('Результат перевода в 10-ю СС\n' + str(fib_to_dec(a)))
