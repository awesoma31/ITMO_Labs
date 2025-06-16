/* heap-1.c */

#include <stdbool.h>
#include <stddef.h>
#include <stdio.h>

#define HEAP_BLOCKS 16
#define BLOCK_CAPACITY 1024

enum block_status { BLK_FREE = 0, BLK_ONE, BLK_FIRST, BLK_CONT, BLK_LAST };

struct heap {
    struct block {
        char contents[BLOCK_CAPACITY];
    } blocks[HEAP_BLOCKS];

    enum block_status status[HEAP_BLOCKS];
} global_heap = {0};

struct block_id {
    size_t ind;
    size_t value;
    bool valid;
    struct heap *heap;
};

struct block_id block_id_new(const size_t value, struct heap *from) {
    return (struct block_id){.valid = true, .value = value, .heap = from};
}

struct block_id block_id_invalid() {
    return (struct block_id){.valid = false};
}

bool block_id_is_valid(struct block_id bid) {
    return bid.valid && bid.value < HEAP_BLOCKS;
}

/* Find block */

bool block_is_free(const struct block_id bid) {
    if (!block_id_is_valid(bid))
        return false;
    return bid.heap->status[bid.value] == BLK_FREE;
}

/* Allocate */
struct block_id block_allocate(struct heap *heap, const size_t size) {
    if (size == 1) {
        for (size_t i = 0; i < HEAP_BLOCKS; i++) {
            if (heap->status[i] == BLK_FREE) {
                heap->status[i] = BLK_ONE;
                struct block_id res = block_id_new(size, heap);
                res.ind = i;
                return res;
            }
        }
    } else {
        size_t startInd = 0;
        size_t curSize = 0;
        for (size_t i = 0; i < HEAP_BLOCKS; i++) {
            if (heap->status[i] == BLK_FREE && curSize == 0) {
                startInd = i;
                curSize = 1;
            } else if (heap->status[i] == BLK_FREE) {
                curSize++;
                if (curSize == size) {
                    heap->status[startInd] = BLK_FIRST;
                    heap->status[startInd + curSize - 1] = BLK_LAST;
                    for (size_t j = startInd + 1; j < startInd + curSize - 1; j++) {
                        heap->status[j] = BLK_CONT;
                    }
                    struct block_id result = block_id_new(size, heap);
                    result.ind = startInd;
                    return result;
                }
            } else {
                curSize = 0;
            }
        }
    }
    return block_id_invalid();
}

/* Printer */
const char *block_repr(const struct block_id b) {
    static const char *const repr[] = {
        [BLK_FREE] = " .",
        [BLK_ONE] = " *",
        [BLK_FIRST] = "[=",
        [BLK_LAST] = "=]",
        [BLK_CONT] = " ="
    };
    if (b.valid)
        return repr[b.heap->status[b.value]];
    return "INVALID";
}

void block_debug_info(struct block_id b, FILE *f) {
    fprintf(f, "%s", block_repr(b));
}

void block_foreach_printer(struct heap *h, const size_t count,
                           void printer(struct block_id, FILE *f), FILE *f) {
    for (size_t c = 0; c < count; c++)
        printer(block_id_new(c, h), f);
}

void heap_debug_info(struct heap *h, FILE *f) {
    block_foreach_printer(h, HEAP_BLOCKS, block_debug_info, f);
    fprintf(f, "\n");
}

void block_free(const struct block_id bid) {
    if (!block_id_is_valid(bid)) {
        fprintf(stderr, "Error: Invalid block ID\n");
        return;
    }

    enum block_status *status = bid.heap->status;

    switch (status[bid.ind]) {
        case BLK_ONE:
            status[bid.ind] = BLK_FREE;
        break;
        case BLK_FIRST: {
            size_t i = bid.ind;
            status[i++] = BLK_FREE;
            while (i < HEAP_BLOCKS && status[i] == BLK_CONT) {
                status[i++] = BLK_FREE;
            }
            if (i < HEAP_BLOCKS && status[i] == BLK_LAST) {
                status[i] = BLK_FREE;
            } else {
                fprintf(stderr, "Error: Corrupted block chain\n");
            }
            break;
        }
        case BLK_CONT:
        case BLK_LAST:
            fprintf(stderr, "Error: Cannot free a middle or last block directly\n");
        break;
        default:
            fprintf(stderr, "Error: Block is already free\n");
        break;
    }
}

int main() {
    struct block_id bid1 = block_allocate(&global_heap, 1);
    const struct block_id bid2 = block_allocate(&global_heap, 2);
    struct block_id bid3 = block_allocate(&global_heap, 3);
    const struct block_id bid4 = block_allocate(&global_heap, 4);

    block_free(bid4);
    block_free(bid2);

    heap_debug_info(&global_heap, stdout);
    return 0;
}
