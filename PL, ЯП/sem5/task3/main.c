#include <inttypes.h>
#include <stdio.h>
#include <stdlib.h>


#define _print(type, x) type##_print(x)

#define print(x)                                                        \
_Generic((x),                                                         \
  int : int_print(x),                                  \
  double : double_print(x),                                    \
  default : error("Unsupported operation"))

#define DEFINE_LIST(type)                                               \
  struct list_##type {                                                  \
    type value;                                                         \
    struct list_##type* next;                                           \
  };                                                                    \
                                                                        \
  struct list_##type* list_##type##_push(struct list_##type* head, type value) { \
    struct list_##type* new_node = malloc(sizeof(struct list_##type));  \
    if (!new_node) {                                                    \
      perror("Failed to allocate memory");                            \
      exit(EXIT_FAILURE);                                               \
    }                                                                   \
    new_node->value = value;                                            \
    new_node->next = NULL;                                              \
                                                                        \
    if (!head) {                                                        \
      return new_node;                                                  \
    }                                                                   \
                                                                        \
    struct list_##type* current = head;                                 \
    while (current->next) {                                             \
      current = current->next;                                          \
    }                                                                   \
    current->next = new_node;                                           \
    return head;                                                        \
  }                                                                     \
                                                                        \
  void list_##type##_print(struct list_##type* head) {                  \
    struct list_##type* current = head;                                 \
    while (current) {\
      print(current->value);                                            \
      fprintf(stdout, "\n");                                             \
      current = current->next;                                          \
    }                                                                   \
}

#define list_push(head, value)                                           \
  _Generic((value),                                                      \
           int: list_int_push,                                           \
           double: list_double_push                                      \
          )(head, value)

#define list_print(head)                                                 \
  _Generic((head),                                                       \
           struct list_int*: list_int_print,                             \
           struct list_double*: list_double_print                        \
          )(head)


void int_print(int64_t i) { printf("%" PRId64, i); }
void double_print(double d) { printf("%lf", d); }
void newline_print() { puts("\n"); }

void error(const char *s) {
    fprintf(stderr, "%s", s);
    abort();
}

DEFINE_LIST(int)
DEFINE_LIST(double)

int main() {
  struct list_int *int_list = NULL;
  int_list = list_push(int_list, 10);
  int_list = list_push(int_list, 20);
  int_list = list_push(int_list, 30);
  printf("int list:\n");
  list_print(int_list);

  struct list_double *double_list = NULL;
  double_list = list_push(double_list, 1.1);
  double_list = list_push(double_list, 2.2);
  printf("double list:\n");
  list_print(double_list);

  struct list_int *another_int_list = NULL;
  another_int_list = list_push(another_int_list, 1);
  another_int_list = list_push(another_int_list, 2);
  another_int_list = list_push(another_int_list, 3);
  another_int_list = list_push(another_int_list, 4);
  printf("int list 2:\n");
  list_print(another_int_list);

  return 0;
}
