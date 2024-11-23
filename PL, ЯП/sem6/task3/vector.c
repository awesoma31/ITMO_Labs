#include <inttypes.h>
#include <stdbool.h>

#include "vector.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

struct vector {
    int64_t *array;
    size_t capacity;
    size_t size;
};

#define MIN(x, y) (((x) < (y)) ? (x) : (y))

struct vector *vector_create(const size_t size) {
    struct vector *result = malloc(sizeof(struct vector));
    if (!result) return NULL;
    result->array = malloc(sizeof(int64_t) * size);
    if (!result->array) {
        free(result);
        return NULL;
    }
    result->size = 0;
    result->capacity = size;
    return result;
}

void vector_free(struct vector *array) {
    if (array) {
        free(array->array);
        free(array);
    }
}

size_t vector_get_size(struct vector *array) {
    return array ? array->size : 0;
}

size_t vector_get_capacity(struct vector *array) {
    return array ? array->capacity : 0;
}

void vector_set_size(struct vector *array, size_t new_count) {
    if (array) {
        array->size = new_count;
    }
}

void vector_set_capacity(struct vector *array, size_t new_capacity) {
    if (array) {
        int64_t *new_array = realloc(array->array, sizeof(int64_t) * new_capacity);
        if (new_array) {
            array->array = new_array;
            array->capacity = new_capacity;
            if (array->size > new_capacity) {
                array->size = new_capacity;
            }
        }
    }
}

bool vector_setter(struct vector *array, size_t index, int64_t value) {
    if (!array) return false;
    if (index >= array->capacity) {
        vector_set_capacity(array, array->capacity * 2);
    }
    if (index >= array->capacity) return false;
    if (index >= array->size) array->size = index + 1;
    array->array[index] = value;
    return true;
}

bool vector_add_to_end(struct vector *array, int64_t value) {
    if (!array) return false;
    if (array->size + 1 >= array->capacity) {
        vector_set_capacity(array, array->capacity * 2);
    }
    array->array[array->size++] = value;
    return true;
}

struct vector *vector_add_vector_to_end(struct vector *array1, struct vector *array2) {
    if (!array1 || !array2) return NULL;
    for (size_t i = 0; i < array2->size; i++) {
        vector_add_to_end(array1, array2->array[i]);
    }
    return array1;
}

void vector_print_all(const struct vector *array) {
    if (!array) return;
    for (size_t i = 0; i < array->size; i++) {
        printf("%" PRId64 " ", array->array[i]);
    }
    printf("\n");
}

void vector_print_to_file_array(const struct vector *array, FILE *file) {
    if (!array || !file) return;
    for (size_t i = 0; i < array->size; i++) {
        fprintf(file, "%" PRId64 " ", array->array[i]);
    }
}

void vector_print_capacity(const struct vector *array) {
    if (!array) return;
    printf("Capacity = %zu\n", array->capacity);
}

void vector_print_count(const struct vector *array) {
    if (!array) return;
    printf("Count = %zu\n", array->size);
}
