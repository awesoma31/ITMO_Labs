#ifndef TETRIS_H
#define TETRIS_H

#include "stm32f4xx_hal.h"
#include <stdint.h>
#include <stdbool.h>

// Размеры игрового поля
#define BOARD_WIDTH  10
#define BOARD_HEIGHT 20
#define CELL_SIZE    3  // размер клетки в пикселях

// Позиция игрового поля на экране
#define BOARD_X      20
#define BOARD_Y      5

// Типы фигур Тетрис
typedef enum {
    TETRIS_I = 0,
    TETRIS_O,
    TETRIS_T,
    TETRIS_S,
    TETRIS_Z,
    TETRIS_J,
    TETRIS_L,
    TETRIS_NONE
} TetrisPieceType;

// Структура для фигуры
typedef struct {
    int8_t x, y;
    TetrisPieceType type;
    uint8_t rotation;
} TetrisPiece;

// Структура игры
typedef struct {
    uint8_t board[BOARD_HEIGHT][BOARD_WIDTH];
    TetrisPiece current_piece;
    TetrisPiece next_piece;
    uint32_t score;
    uint32_t level;
    uint32_t lines_cleared;
    bool game_over;
    bool paused;
    uint32_t last_drop_time;  // Время последнего автоматического падения
    bool fast_drop;  // Флаг ускоренного падения при удержании кнопки
} TetrisGame;

// Управление игрой
void Tetris_Init(TetrisGame* game);
void Tetris_NewPiece(TetrisGame* game);
bool Tetris_MovePiece(TetrisGame* game, int8_t dx, int8_t dy);
bool Tetris_RotatePiece(TetrisGame* game);
bool Tetris_RotatePieceCounterClockwise(TetrisGame* game);
bool Tetris_DropPiece(TetrisGame* game);
void Tetris_LockPiece(TetrisGame* game);
uint8_t Tetris_ClearLines(TetrisGame* game);
bool Tetris_CheckCollision(TetrisGame* game, int8_t x, int8_t y, uint8_t rotation);
void Tetris_Update(TetrisGame* game);
void Tetris_Draw(TetrisGame* game);

// Получить форму фигуры
const uint8_t* Tetris_GetPieceShape(TetrisPieceType type, uint8_t rotation);

#endif // TETRIS_H

