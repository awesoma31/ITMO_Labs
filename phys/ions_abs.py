import math
import random
import sys

import pygame

# Инициализация Pygame
pygame.init()

# Настройки экрана
WIDTH, HEIGHT = 800, 600
screen = pygame.display.set_mode((WIDTH, HEIGHT))
pygame.display.set_caption("Шарик и ионы")

# Цвета
WHITE = (255, 255, 255)
RED = (255, 0, 0)
BLUE = (0, 0, 255)
BLACK = (0, 0, 0)
GREEN = (0, 255, 0)


# Класс шарика
class Ball:
    def __init__(self, radius=15, mass=1, speed=5):
        self.radius = radius
        self.mass = mass
        self.x = random.randint(radius, WIDTH - radius)
        self.y = random.randint(radius, HEIGHT - radius)

        # Начальная скорость (случайное направление с заданной величиной)
        angle = random.uniform(0, 2 * math.pi)
        self.dx = math.cos(angle) * speed
        self.dy = math.sin(angle) * speed

        self.color = GREEN

    def move(self):
        # Движение шарика
        self.x += self.dx
        self.y += self.dy

        # Отражение от стен
        if self.x - self.radius <= 0 or self.x + self.radius >= WIDTH:
            self.dx *= -1
        if self.y - self.radius <= 0 or self.y + self.radius >= HEIGHT:
            self.dy *= -1

    def draw(self):
        pygame.draw.circle(screen, self.color, (int(self.x), int(self.y)), self.radius)


# Класс иона
class Ion:
    def __init__(self, x, y, radius=10):
        self.x = x
        self.y = y
        self.radius = radius
        self.color = BLUE

    def draw(self):
        pygame.draw.circle(screen, self.color, (self.x, self.y), self.radius)


# Функция проверки столкновения и отражения
def check_collision(ball, ion):
    # Расстояние между центрами
    dx = ball.x - ion.x
    dy = ball.y - ion.y
    distance = math.sqrt(dx**2 + dy**2)

    # Если есть столкновение
    if distance < ball.radius + ion.radius:
        # Нормализованный вектор от иона к шарику
        if distance > 0:
            nx = dx / distance
            ny = dy / distance
        else:
            nx, ny = 1, 0  # На случай совпадения позиций

        # Проекция скорости на нормаль
        projection = ball.dx * nx + ball.dy * ny

        # Отражение (меняем направление проекции на нормаль)
        ball.dx -= 2 * projection * nx
        ball.dy -= 2 * projection * ny

        # Отодвигаем шарик, чтобы он не застрял внутри иона
        overlap = (ball.radius + ion.radius) - distance
        ball.x += overlap * nx * 0.5
        ball.y += overlap * ny * 0.5


# Создание объектов с настраиваемыми параметрами
ball = Ball(radius=20, mass=2, speed=7)  # Можно менять параметры

ions = []
# Создаем несколько статичных ионов с настраиваемым радиусом
for _ in range(15):
    x = random.randint(30, WIDTH - 30)
    y = random.randint(30, HEIGHT - 30)
    ions.append(Ion(x, y, radius=random.randint(8, 15)))  # Можно менять радиус

# Основной цикл
clock = pygame.time.Clock()
RUNNING = True

while RUNNING:
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            RUNNING = False
        elif event.type == pygame.KEYDOWN:
            if event.key == pygame.K_r:  # Перезапуск по нажатию R
                ball = Ball(radius=20, mass=2, speed=7)

    # Очистка экрана
    screen.fill(BLACK)

    # Движение и отрисовка
    ball.move()
    ball.draw()

    # Проверка столкновений с каждым ионом
    for ion in ions:
        check_collision(ball, ion)
        ion.draw()

    # Отображение информации
    font = pygame.font.SysFont(None, 24)
    info_text = f"Шарик: радиус={ball.radius}, скорость={math.sqrt(ball.dx**2 + ball.dy**2):.1f}"
    text_surface = font.render(info_text, True, WHITE)
    screen.blit(text_surface, (10, 10))

    # Обновление экрана
    pygame.display.flip()
    clock.tick(60)

pygame.quit()
sys.exit()
