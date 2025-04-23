import math
import random
import sys

import pygame

# Инициализация Pygame
pygame.init()

# Настройки экрана
WIDTH, HEIGHT = 800, 600
screen = pygame.display.set_mode((WIDTH, HEIGHT))
pygame.display.set_caption("Шарик и ионы с физикой столкновений")

# Цвета
WHITE = (255, 255, 255)
BLUE = (0, 0, 255)
BLACK = (0, 0, 0)
GREEN = (0, 255, 0)
RED = (255, 0, 0)

# Параметры физики
RESTITUTION = 0.8  # Коэффициент восстановления
FRICTION = 0.02  # Потеря скорости при столкновении
MIN_SPEED = 0.1  # Минимальная скорость, ниже которой шарик останавливается


class Ball:
    def __init__(self, radius=15, mass=1, speed=5):
        self.radius = radius
        self.mass = mass
        self.reset(speed)
        self.color = GREEN

    def reset(self, speed):
        self.x = random.randint(self.radius, WIDTH - self.radius)
        self.y = random.randint(self.radius, HEIGHT - self.radius)
        angle = random.uniform(0, 2 * math.pi)
        self.dx = math.cos(angle) * speed
        self.dy = math.sin(angle) * speed
        self.speed_history = [speed]

    def move(self):
        # Проверка на выход за границы с корректировкой позиции
        if self.x - self.radius < 0:
            self.x = self.radius
            self.dx *= -RESTITUTION
        elif self.x + self.radius > WIDTH:
            self.x = WIDTH - self.radius
            self.dx *= -RESTITUTION

        if self.y - self.radius < 0:
            self.y = self.radius
            self.dy *= -RESTITUTION
        elif self.y + self.radius > HEIGHT:
            self.y = HEIGHT - self.radius
            self.dy *= -RESTITUTION

        # Применение движения
        self.x += self.dx
        self.y += self.dy

        # Остановка при очень малой скорости
        speed = math.sqrt(self.dx**2 + self.dy**2)
        if speed < MIN_SPEED:
            self.dx = 0
            self.dy = 0

    def draw(self):
        pygame.draw.circle(screen, self.color, (int(self.x), int(self.y)), self.radius)

        # Рисуем вектор скорости если скорость достаточная
        if math.sqrt(self.dx**2 + self.dy**2) > MIN_SPEED:
            end_x = self.x + self.dx * 5
            end_y = self.y + self.dy * 5
            pygame.draw.line(screen, WHITE, (self.x, self.y), (end_x, end_y), 2)


class Ion:
    def __init__(self, x, y, radius=10):
        self.x = x
        self.y = y
        self.radius = radius
        self.color = BLUE

    def draw(self):
        pygame.draw.circle(screen, self.color, (self.x, self.y), self.radius)


def check_collision(ball, ion):
    dx = ball.x - ion.x
    dy = ball.y - ion.y
    distance_sq = dx**2 + dy**2
    min_distance = ball.radius + ion.radius

    if distance_sq < min_distance**2:
        distance = math.sqrt(distance_sq)

        # Нормализованный вектор
        if distance > 0:
            nx = dx / distance
            ny = dy / distance
        else:
            nx, ny = 1, 0

        # Проекция скорости на нормаль
        projection = ball.dx * nx + ball.dy * ny

        # Неупругое отражение
        ball.dx -= (1 + RESTITUTION) * projection * nx
        ball.dy -= (1 + RESTITUTION) * projection * ny

        # Дополнительное трение
        ball.dx *= 1 - FRICTION
        ball.dy *= 1 - FRICTION

        # Коррекция позиции (безопасная)
        overlap = min_distance - distance
        ball.x += overlap * nx * 0.51
        ball.y += overlap * ny * 0.51

        # Запоминаем скорость
        ball.speed_history.append(math.sqrt(ball.dx**2 + ball.dy**2))
        if len(ball.speed_history) > 100:
            ball.speed_history.pop(0)


# Создание объектов
ball = Ball(radius=20, mass=2, speed=10)
ions = [
    Ion(
        random.randint(30, WIDTH - 30),
        random.randint(30, HEIGHT - 30),
        radius=random.randint(8, 15),
    )
    for _ in range(15)
]

# Основной цикл
clock = pygame.time.Clock()
running = True

while running:
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            running = False
        elif event.type == pygame.KEYDOWN:
            if event.key == pygame.K_r:
                ball.reset(10)
            elif event.key == pygame.K_UP:
                RESTITUTION = min(0.95, RESTITUTION + 0.05)
            elif event.key == pygame.K_DOWN:
                RESTITUTION = max(0.05, RESTITUTION - 0.05)

    # Обновление
    ball.move()
    for ion in ions:
        check_collision(ball, ion)

    # Отрисовка
    screen.fill(BLACK)
    ball.draw()
    for ion in ions:
        ion.draw()

    # Информация
    font = pygame.font.SysFont(None, 24)
    current_speed = math.sqrt(ball.dx**2 + ball.dy**2)
    info_text = [
        f"Шарик: V ={current_speed:.1f}",
        f"Коэф. восстановления: {RESTITUTION:.2f} (UP/DOWN для изменения)",
        f"Потери при столкновении: {FRICTION*100:.0f}%",
        "r - restart",
    ]

    for i, text in enumerate(info_text):
        text_surface = font.render(text, True, WHITE)
        screen.blit(text_surface, (10, 10 + i * 25))

    # График скорости
    if len(ball.speed_history) > 1:
        max_speed = max(ball.speed_history) or 1
        points = [
            (10 + i * 5, 150 - (s / max_speed) * 50)
            for i, s in enumerate(ball.speed_history)
        ]
        pygame.draw.lines(screen, RED, False, points, 2)

    pygame.display.flip()
    clock.tick(60)

pygame.quit()
sys.exit()
