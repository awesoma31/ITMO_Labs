#ifndef TM1637_H
#define TM1637_H

#include <stdio.h>
#include <stdint.h>
#include <string.h>

#include "main.h"
#include "modes.h"

void initKeyboard();
char readKey();
void scanKeyboard();

extern char lastKey;
extern uint32_t lastScanTime;

#endif