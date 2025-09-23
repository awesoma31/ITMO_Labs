#ifndef MODES_H
#define MODES_H

typedef enum {
    MODE_NONE = 0,
    MODE_B,
    MODE_D,
    MODE_H
} Mode_t;

extern volatile Mode_t Mode;
#endif