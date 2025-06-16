#include <stddef.h>
#include <stdio.h>
#include "vector.h"

int main() {
    struct vector *array = vector_create(5);

    vector_print_capacity(array);
    vector_print_count(array);

    for (size_t i = 0; i < 15; i++) {
        vector_setter(array, i, i);
    }

    vector_print_capacity(array);
    vector_print_count(array);

    vector_add_to_end(array, -5);

    vector_print_all(array);

    vector_print_capacity(array);
    vector_print_count(array);

    struct vector *array2 = vector_create(100);
    for (size_t i = 0; i < 10; i++) {
        vector_setter(array2, i, i - i * i);
    }

    vector_print_all(array2);

    vector_add_vector_to_end(array, array2);

    vector_print_all(array);
    vector_print_all(array2);

    vector_print_capacity(array2);
    vector_print_count(array2);

    vector_set_capacity(array2, 14);
    vector_print_all(array2);

    vector_print_capacity(array2);
    vector_print_count(array2);

    vector_set_capacity(array2, 3);
    vector_print_all(array2);

    vector_print_capacity(array2);
    vector_print_count(array2);

    FILE *fptr = fopen("123.txt", "w");
    if (fptr) {
        vector_print_to_file_array(array, fptr);
        fclose(fptr);
    }

    vector_free(array2);
    vector_free(array);

    return 0;
}
