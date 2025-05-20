import math
import sys

import pygame

pygame.init()

WIDTH, HEIGHT = 800, 600
screen = pygame.display.set_mode((WIDTH, HEIGHT))
pygame.display.set_caption("Снаряд с импульсом и цели")

BG_COLOR = (255, 255, 255)
PROJECTILE_COLOR = (255, 50, 50)
TARGET_COLOR = (50, 50, 255)


class MovingCircle:
    def __init__(self, x, y, radius, mass, vel_x=0, vel_y=0, color=(0, 0, 0)):
        self.pos = pygame.math.Vector2(x, y)
        self.vel = pygame.math.Vector2(vel_x, vel_y)
        self.radius = radius
        self.mass = mass
        self.color = color

    def update_position(self):
        self.pos += self.vel
        self.bounce_if_needed()

    def bounce_if_needed(self):
        if self.pos.x - self.radius < 0:
            self.pos.x = self.radius
            self.vel.x *= -1
        elif self.pos.x + self.radius > WIDTH:
            self.pos.x = WIDTH - self.radius
            self.vel.x *= -1
        if self.pos.y - self.radius < 0:
            self.pos.y = self.radius
            self.vel.y *= -1
        elif self.pos.y + self.radius > HEIGHT:
            self.pos.y = HEIGHT - self.radius
            self.vel.y *= -1

    def render(self, surface):
        pygame.draw.circle(
            surface, self.color, (int(self.pos.x), int(self.pos.y)), self.radius
        )


def handle_collision(a: MovingCircle, b: MovingCircle):
    delta = b.pos - a.pos
    dist = delta.length()
    if dist == 0 or dist > a.radius + b.radius:
        return False

    normal = delta.normalize()
    rel_vel = b.vel - a.vel
    speed = rel_vel.dot(normal)

    if speed >= 0:
        return False  # Уже расходятся

    # Упругое столкновение с сохранением импульса
    impulse = (2 * speed) / (a.mass + b.mass)
    a.vel += impulse * b.mass * normal
    b.vel -= impulse * a.mass * normal

    # Избегаем залипания
    overlap = a.radius + b.radius - dist
    a.pos -= normal * (overlap * b.mass / (a.mass + b.mass))
    b.pos += normal * (overlap * a.mass / (a.mass + b.mass))

    return True


# Тяжёлый снаряд
projectile = MovingCircle(
    110, HEIGHT // 2 + 15, radius=35, mass=5, vel_x=5, vel_y=0, color=PROJECTILE_COLOR
)

# Лёгкие цели (вершины правильного шестиугольника)
targets = [
    MovingCircle(770, 300, radius=20, mass=20, color=TARGET_COLOR),  # right
    MovingCircle(710, 196, radius=20, mass=20, color=TARGET_COLOR),  # upper-right
    MovingCircle(590, 196, radius=20, mass=20, color=TARGET_COLOR),  # upper-left
    # MovingCircle(530, 300, radius=20, mass=20, color=TARGET_COLOR),  # left
    MovingCircle(590, 404, radius=20, mass=20, color=TARGET_COLOR),  # lower-left
    MovingCircle(710, 404, radius=20, mass=20, color=TARGET_COLOR),  # lower-right
]

clock = pygame.time.Clock()
running = True


def update_screen():
    screen.fill(BG_COLOR)
    projectile.render(screen)
    for target in targets:
        target.render(screen)
    pygame.display.update()


while running:
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            running = False

    projectile.update_position()

    # Удаляем цели, которые были задеты
    new_targets = []
    for target in targets:
        if not handle_collision(projectile, target):
            target.update_position()
            new_targets.append(target)
    targets = new_targets

    update_screen()
    clock.tick(60)

pygame.quit()
sys.exit()
