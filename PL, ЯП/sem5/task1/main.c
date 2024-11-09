#include <stdio.h>

#define print_var(x) printf(#x " is %d\n", x)

#define CONST_VALUE 10

int main() {
    int a = 5;

    print_var(a);

    print_var(42);

    print_var(CONST_VALUE);

    return 0;
}
