#include "tetris.h"
#include "oled.h"
#include "buzzer.h"
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

// Формы фигур (4x4 матрица, каждая ротация = массив из 16 элементов)
static const uint8_t piece_shapes[7][4][16] = {
    // I - палка
    {
        {0,0,0,0, 1,1,1,1, 0,0,0,0, 0,0,0,0},  // горизонтально
        {0,0,1,0, 0,0,1,0, 0,0,1,0, 0,0,1,0},  // вертикально
        {0,0,0,0, 0,0,0,0, 1,1,1,1, 0,0,0,0},  // горизонтально (смещено)
        {0,1,0,0, 0,1,0,0, 0,1,0,0, 0,1,0,0}   // вертикально (смещено)
    },
    // O - квадрат
    {
        {0,1,1,0, 0,1,1,0, 0,0,0,0, 0,0,0,0},
        {0,1,1,0, 0,1,1,0, 0,0,0,0, 0,0,0,0},
        {0,1,1,0, 0,1,1,0, 0,0,0,0, 0,0,0,0},
        {0,1,1,0, 0,1,1,0, 0,0,0,0, 0,0,0,0}
    },
    // T - буква T
    {
        {0,1,0,0, 1,1,1,0, 0,0,0,0, 0,0,0,0},
        {0,1,0,0, 0,1,1,0, 0,1,0,0, 0,0,0,0},
        {0,0,0,0, 1,1,1,0, 0,1,0,0, 0,0,0,0},
        {0,1,0,0, 1,1,0,0, 0,1,0,0, 0,0,0,0}
    },
    // S - зигзаг
    {
        {0,1,1,0, 1,1,0,0, 0,0,0,0, 0,0,0,0},
        {0,1,0,0, 0,1,1,0, 0,0,1,0, 0,0,0,0},
        {0,0,0,0, 0,1,1,0, 1,1,0,0, 0,0,0,0},
        {1,0,0,0, 1,1,0,0, 0,1,0,0, 0,0,0,0}
    },
    // Z - зигзаг (обратный)
    {
        {1,1,0,0, 0,1,1,0, 0,0,0,0, 0,0,0,0},
        {0,0,1,0, 0,1,1,0, 0,1,0,0, 0,0,0,0},
        {0,0,0,0, 1,1,0,0, 0,1,1,0, 0,0,0,0},
        {0,1,0,0, 1,1,0,0, 1,0,0,0, 0,0,0,0}
    },
    // J - буква J
    {
        {1,0,0,0, 1,1,1,0, 0,0,0,0, 0,0,0,0},
        {0,1,1,0, 0,1,0,0, 0,1,0,0, 0,0,0,0},
        {0,0,0,0, 1,1,1,0, 0,0,1,0, 0,0,0,0},
        {0,1,0,0, 0,1,0,0, 1,1,0,0, 0,0,0,0}
    },
    // L - буква L
    {
        {0,0,1,0, 1,1,1,0, 0,0,0,0, 0,0,0,0},
        {0,1,0,0, 0,1,0,0, 0,1,1,0, 0,0,0,0},
        {0,0,0,0, 1,1,1,0, 1,0,0,0, 0,0,0,0},
        {1,1,0,0, 0,1,0,0, 0,1,0,0, 0,0,0,0}
    }
};

void Tetris_Init(TetrisGame* game) {
    memset(game, 0, sizeof(TetrisGame));
    game->current_piece.type = TETRIS_NONE;
    game->next_piece.type = TETRIS_NONE;
    game->last_drop_time = HAL_GetTick();
    Tetris_NewPiece(game);
}

void Tetris_NewPiece(TetrisGame* game) {
    // Переместить следующую фигуру в текущую
    game->current_piece = game->next_piece;
    
    // Создать новую случайную фигуру
    TetrisPieceType new_type = (TetrisPieceType)(rand() % 7);
    game->next_piece.type = new_type;
    game->next_piece.x = BOARD_WIDTH / 2 - 2;
    game->next_piece.y = 0;
    game->next_piece.rotation = 0;
    
    // Если текущая фигура пустая, создать её тоже
    if (game->current_piece.type == TETRIS_NONE) {
        game->current_piece = game->next_piece;
        TetrisPieceType next_new_type = (TetrisPieceType)(rand() % 7);
        game->next_piece.type = next_new_type;
    }
    
    // Проверить коллизию при появлении
    if (Tetris_CheckCollision(game, game->current_piece.x, game->current_piece.y, game->current_piece.rotation)) {
        game->game_over = true;
    }
}

bool Tetris_CheckCollision(TetrisGame* game, int8_t x, int8_t y, uint8_t rotation) {
    const uint8_t* shape = Tetris_GetPieceShape(game->current_piece.type, rotation);
    
    for (int py = 0; py < 4; py++) {
        for (int px = 0; px < 4; px++) {
            if (shape[py * 4 + px]) {
                int8_t board_x = x + px;
                int8_t board_y = y + py;
                
                // Проверка границ
                if (board_x < 0 || board_x >= BOARD_WIDTH || board_y >= BOARD_HEIGHT) {
                    return true;
                }
                
                // Проверка столкновения с уже установленными блоками
                if (board_y >= 0 && game->board[board_y][board_x]) {
                    return true;
                }
            }
        }
    }
    return false;
}

bool Tetris_MovePiece(TetrisGame* game, int8_t dx, int8_t dy) {
    if (game->game_over || game->paused) return false;
    
    int8_t new_x = game->current_piece.x + dx;
    int8_t new_y = game->current_piece.y + dy;
    
    if (!Tetris_CheckCollision(game, new_x, new_y, game->current_piece.rotation)) {
        game->current_piece.x = new_x;
        game->current_piece.y = new_y;
        return true;
    }
    return false;
}

bool Tetris_RotatePiece(TetrisGame* game) {
    if (game->game_over || game->paused) return false;
    
    uint8_t new_rotation = (game->current_piece.rotation + 1) % 4;
    
    if (!Tetris_CheckCollision(game, game->current_piece.x, game->current_piece.y, new_rotation)) {
        game->current_piece.rotation = new_rotation;
        return true;
    }
    return false;
}

bool Tetris_RotatePieceCounterClockwise(TetrisGame* game) {
    if (game->game_over || game->paused) return false;
    
    uint8_t new_rotation = (game->current_piece.rotation + 3) % 4; // +3 эквивалентно -1 по модулю 4
    
    if (!Tetris_CheckCollision(game, game->current_piece.x, game->current_piece.y, new_rotation)) {
        game->current_piece.rotation = new_rotation;
        return true;
    }
    return false;
}

bool Tetris_DropPiece(TetrisGame* game) {
    if (game->game_over || game->paused) return false;
    
    return Tetris_MovePiece(game, 0, 1);
}

void Tetris_LockPiece(TetrisGame* game) {
    const uint8_t* shape = Tetris_GetPieceShape(game->current_piece.type, game->current_piece.rotation);
    
    for (int py = 0; py < 4; py++) {
        for (int px = 0; px < 4; px++) {
            if (shape[py * 4 + px]) {
                int8_t board_x = game->current_piece.x + px;
                int8_t board_y = game->current_piece.y + py;
                
                if (board_x >= 0 && board_x < BOARD_WIDTH && 
                    board_y >= 0 && board_y < BOARD_HEIGHT) {
                    game->board[board_y][board_x] = game->current_piece.type + 1;
                }
            }
        }
    }
}

uint8_t Tetris_ClearLines(TetrisGame* game) {
    uint8_t lines_cleared = 0;
    
    for (int y = BOARD_HEIGHT - 1; y >= 0; y--) {
        bool full_line = true;
        for (int x = 0; x < BOARD_WIDTH; x++) {
            if (!game->board[y][x]) {
                full_line = false;
                break;
            }
        }
        
        if (full_line) {
            // Сдвинуть все линии вниз
            for (int move_y = y; move_y > 0; move_y--) {
                for (int x = 0; x < BOARD_WIDTH; x++) {
                    game->board[move_y][x] = game->board[move_y - 1][x];
                }
            }
            // Очистить верхнюю линию
            for (int x = 0; x < BOARD_WIDTH; x++) {
                game->board[0][x] = 0;
            }
            lines_cleared++;
            y++; // Проверить эту же линию снова
        }
    }
    
    if (lines_cleared > 0) {
        // Воспроизвести звук при удалении линий (484 Hz)
        Buzzer_Set_Freq(484);
        Buzzer_Set_Volume(BUZZER_VOLUME_MAX);
        // Звук будет выключен в задаче GameTask через небольшую задержку
    }
    
    game->lines_cleared += lines_cleared;
    game->score += lines_cleared * 100 * (game->level + 1);
    game->level = game->lines_cleared / 10;
    
    return lines_cleared;
}

void Tetris_Update(TetrisGame* game) {
    if (game->game_over || game->paused) return;
    
    // Автоматическое падение фигуры
    uint32_t current_time = HAL_GetTick();
    
    // Инициализация времени при первом вызове
    if (game->last_drop_time == 0) {
        game->last_drop_time = current_time;
    }
    
    uint32_t drop_interval = 100 - (game->level * 50); // Ускоряется с уровнем
    //uint32_t drop_interval = 1000 - (game->level * 50);
    if (drop_interval < 100) drop_interval = 100;
    
    // Если удерживается кнопка вниз, ускоряем падение
    if (game->fast_drop) {
        drop_interval = 50; // Быстрое падение при удержании
    }
    
    if (current_time - game->last_drop_time >= drop_interval) {
        if (!Tetris_DropPiece(game)) {
            // Фигура не может упасть дальше
            Tetris_LockPiece(game);
            Tetris_ClearLines(game);
            Tetris_NewPiece(game);
        }
        game->last_drop_time = current_time;
    }
}

void Tetris_Draw(TetrisGame* game) {
    oled_Fill(Black);
    
    // Рисуем границы игрового поля
    uint8_t board_pixel_width = BOARD_WIDTH * CELL_SIZE;
    uint8_t board_pixel_height = BOARD_HEIGHT * CELL_SIZE;
    
    // Левая граница
    oled_DrawVLine(BOARD_Y, BOARD_Y + board_pixel_height, BOARD_X, White);
    // Правая граница
    oled_DrawVLine(BOARD_Y, BOARD_Y + board_pixel_height, BOARD_X + board_pixel_width, White);
    // Верхняя граница
    oled_DrawHLine(BOARD_X, BOARD_X + board_pixel_width, BOARD_Y, White);
    // Нижняя граница
    oled_DrawHLine(BOARD_X, BOARD_X + board_pixel_width, BOARD_Y + board_pixel_height, White);
    
    // Рисуем установленные блоки
    for (int y = 0; y < BOARD_HEIGHT; y++) {
        for (int x = 0; x < BOARD_WIDTH; x++) {
            if (game->board[y][x]) {
                uint8_t px = BOARD_X + x * CELL_SIZE + 1;
                uint8_t py = BOARD_Y + y * CELL_SIZE + 1;
                oled_DrawSquare(px, px + CELL_SIZE - 2, py, py + CELL_SIZE - 2, White);
            }
        }
    }
    
    // Рисуем текущую фигуру
    if (game->current_piece.type != TETRIS_NONE) {
        const uint8_t* shape = Tetris_GetPieceShape(game->current_piece.type, game->current_piece.rotation);
        
        for (int py = 0; py < 4; py++) {
            for (int px = 0; px < 4; px++) {
                if (shape[py * 4 + px]) {
                    int8_t board_x = game->current_piece.x + px;
                    int8_t board_y = game->current_piece.y + py;
                    
                    if (board_x >= 0 && board_x < BOARD_WIDTH && 
                        board_y >= 0 && board_y < BOARD_HEIGHT) {
                        uint8_t screen_x = BOARD_X + board_x * CELL_SIZE + 1;
                        uint8_t screen_y = BOARD_Y + board_y * CELL_SIZE + 1;
                        oled_DrawSquare(screen_x, screen_x + CELL_SIZE - 2, 
                                       screen_y, screen_y + CELL_SIZE - 2, White);
                    }
                }
            }
        }
    }
    
    // Рисуем информацию справа
    char buf[32];
    oled_SetCursor(BOARD_X + board_pixel_width + 5, 5);
    snprintf(buf, sizeof(buf), "Score:%lu", (unsigned long)game->score);
    oled_WriteString(buf, Font_7x10, White);
    
    oled_SetCursor(BOARD_X + board_pixel_width + 5, 17);
    snprintf(buf, sizeof(buf), "Level:%lu", (unsigned long)game->level);
    oled_WriteString(buf, Font_7x10, White);
    
    oled_SetCursor(BOARD_X + board_pixel_width + 5, 29);
    snprintf(buf, sizeof(buf), "Lines:%lu", (unsigned long)game->lines_cleared);
    oled_WriteString(buf, Font_7x10, White);
    
    if (game->game_over) {
        oled_SetCursor(BOARD_X + 10, BOARD_Y + board_pixel_height / 2);
        oled_WriteString("GAME OVER", Font_7x10, White);
    } else if (game->paused) {
        oled_SetCursor(BOARD_X + 15, BOARD_Y + board_pixel_height / 2);
        oled_WriteString("PAUSED", Font_7x10, White);
    }
}

const uint8_t* Tetris_GetPieceShape(TetrisPieceType type, uint8_t rotation) {
    if (type >= TETRIS_NONE) return NULL;
    return (const uint8_t*)piece_shapes[type][rotation];
}

