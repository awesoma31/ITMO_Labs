/* printer.c */

#include <inttypes.h>
#include <stdlib.h>
#include <stdio.h>


struct AST {
    enum AST_type { AST_BINOP, AST_UNOP, AST_LIT } type;

    union {
        struct binop {
            enum binop_type { BIN_PLUS, BIN_MINUS, BIN_MUL, BIN_DIV } type;

            struct AST *left, *right;
        } as_binop;

        struct unop {
            enum unop_type { UN_NEG } type;

            struct AST *operand;
        } as_unop;

        struct literal {
            int64_t value;
        } as_literal;
    };
};

/* DSL */
static struct AST *newnode(struct AST ast) {
    struct AST *const node = malloc(sizeof(struct AST));
    *node = ast;
    return node;
}

struct AST _lit(int64_t value) {
    return (struct AST){AST_LIT, .as_literal = {value}};
}

struct AST *lit(int64_t value) {
    return newnode(_lit(value));
}

struct AST _unop(enum unop_type type, struct AST *operand) {
    return (struct AST){AST_UNOP, .as_unop = {type, operand}};
}

struct AST *unop(enum unop_type type, struct AST *operand) {
    return newnode(_unop(type, operand));
}

struct AST _binop(enum binop_type type, struct AST *left, struct AST *right) {
    return (struct AST){AST_BINOP, .as_binop = {type, left, right}};
}

struct AST *binop(enum binop_type type, struct AST *left, struct AST *right) {
    return newnode(_binop(type, left, right));
}

#define DECLARE_BINOP(fun, code)                                               \
  struct AST *fun(struct AST *left, struct AST *right) {                       \
    return binop(BIN_##code, left, right);                                     \
  }
DECLARE_BINOP(add, PLUS)
DECLARE_BINOP(mul, MUL)
DECLARE_BINOP(sub, MINUS)
DECLARE_BINOP(myDiv, DIV)


#undef DECLARE_BINOP
#define DECLARE_UNOP(fun, code)                                                \
  struct AST *fun(struct AST *operand) {                                       \
    return unop(UN_##code, operand);                                           \
  }
DECLARE_UNOP(neg, NEG)
#undef DECLARE_UNOP
/* printer */

static const char *BINOPS[] = {
    [BIN_PLUS] = "+", [BIN_MINUS] = "-", [BIN_MUL] = "*"
};
static const char *UNOPS[] = {[UN_NEG] = "-"};

typedef void (printer)(FILE *, struct AST *);

void print(FILE *f, struct AST *ast);

void print_binop(FILE *f, struct AST *ast) {
    fprintf(f, "(");
    print(f, ast->as_binop.left);
    fprintf(f, ")");
    fprintf(f, "%s", BINOPS[ast->as_binop.type]);
    fprintf(f, "(");
    print(f, ast->as_binop.right);
    fprintf(f, ")");
}

void print_unop(FILE *f, struct AST *ast) {
    fprintf(f, "(%s", UNOPS[ast->as_unop.type]);
    print(f, ast->as_unop.operand);
    fprintf(f, ")");
}

void print_lit(FILE *f, struct AST *ast) {
    fprintf(f, "%" PRId64, ast->as_literal.value);
}

static printer *ast_printers[] = {
    [AST_BINOP] = print_binop, [AST_UNOP] = print_unop, [AST_LIT] = print_lit
};

void print(FILE *f, struct AST *ast) {
    if (ast)
        ast_printers[ast->type](f, ast);
    else
        fprintf(f, "<NULL>");
}

void free_ast(struct AST *ast) {
    if (!ast) return;
    switch (ast->type) {
        case AST_BINOP:
            free_ast(ast->as_binop.left);
            free_ast(ast->as_binop.right);
            break;
        case AST_UNOP:
            free_ast(ast->as_unop.operand);
            break;
        case AST_LIT:
            break;
    }
    free(ast);
}

int main() {
    // 999 + 728
    struct AST *expr1 = add(lit(999), lit(728));
    print(stdout, expr1);
    printf("\n");
    free_ast(expr1);

    // 4 + 2 * 9
    struct AST *expr2 = add(lit(4), mul(lit(2), lit(9)));
    print(stdout, expr2);
    printf("\n");
    free_ast(expr2);

    // (3 + 5) * (9 / 7)
    struct AST *expr3 = mul(add(lit(3), lit(5)), myDiv(lit(9), lit(7)));
    print(stdout, expr3);
    printf("\n");
    free_ast(expr3);

    return 0;
}
