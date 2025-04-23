import math
import sys

import pygame

pygame.init()

WIDTH, HEIGHT = 800, 600
screen = pygame.display.set_mode((WIDTH, HEIGHT))
pygame.display.set_caption("BALLLLLLS")

# BG_COLOR = (255, 255, 255)
BG_COLOR = "darkgrey"
COLOR_1 = (255, 50, 50)
COLOR_2 = (50, 50, 255)


class Ball:
    def __init__(self, pos_x, pos_y, mass, vel_x, vel_y, radius, color):

        self.pos = pygame.math.Vector2(pos_x, pos_y)

        self.vel = pygame.math.Vector2(vel_x, vel_y)
        self.mass = mass
        self.radius = radius
        self.color = color

    def update_position(self):

        self.pos += self.vel

        self.bounce_if_needed()

    def bounce_if_needed(self):
        # left right
        if self.pos.x - self.radius < 0:
            self.pos.x = self.radius
            self.vel.x *= -1
        elif self.pos.x + self.radius > WIDTH:
            self.pos.x = WIDTH - self.radius
            self.vel.x *= -1

        # top bot
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


def handle_collision(obj1, obj2):

    delta = obj2.pos - obj1.pos
    dist = delta.length()

    if dist == 0:
        return

    if dist > obj1.radius + obj2.radius:
        return

    normal = delta.normalize()

    relative_velocity = obj2.vel - obj1.vel

    speed_along_normal = relative_velocity.dot(normal)

    if speed_along_normal > 0:
        return

    impulse_mag = (2 * speed_along_normal) / (obj1.mass + obj2.mass)

    obj1.vel += impulse_mag * obj2.mass * normal
    obj2.vel -= impulse_mag * obj1.mass * normal

    overlap = (obj1.radius + obj2.radius - dist) / 2
    obj1.pos -= normal * overlap
    obj2.pos += normal * overlap

    angle1 = math.degrees(math.atan2(obj1.vel.y, obj1.vel.x))
    angle2 = math.degrees(math.atan2(obj2.vel.y, obj2.vel.x))  # arctan(vy/vx)

    print(
        f"RED BALL: скорость = ({obj1.vel.x:.2f}, {obj1.vel.y:.2f}), угол = {angle1:.1f}°"
    )
    print(
        f"BLUE BALL: скорость = ({obj2.vel.x:.2f}, {obj2.vel.y:.2f}), угол = {angle2:.1f}°"
    )


circle_a = Ball(101, 330 - 30 * math.sqrt(2) / 2, 10, 5, 0, 30, COLOR_1)
circle_b = Ball(699, 330, 10, -5, 0, 30, COLOR_2)
# circle_a = Ball(200, 330 - 30 * math.sqrt(2), 2, 10, 0, 30, COLOR_1)  # левый шар (выше)
# circle_b = Ball(600, 330, 10, 0, 0, 30, COLOR_2)  # правый шар (ниже)


clock = pygame.time.Clock()
RUNNING = True


def update_screen():
    screen.fill(BG_COLOR)
    circle_a.render(screen)
    circle_b.render(screen)
    pygame.display.update()


while RUNNING:
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            RUNNING = False

    circle_a.update_position()
    circle_b.update_position()

    handle_collision(circle_a, circle_b)

    update_screen()

    clock.tick(60)

pygame.quit()
sys.exit()
