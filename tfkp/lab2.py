import numpy as np
import matplotlib.pyplot as plt

# Этап 1: Инверсия и масштабирование
def step1(z, e):
    return e / z

# Этап 2: Преобразование в полосу
def step2(z):
    return (z - 1) / (z + 1)

# Этап 3: Преобразование полосы в область с вырезами
def step3(z):
    return 0.5 * (z + 1 / z)

# Генерация точек для внешности круга
def generate_external_points(radius, n_radii=10, n_points=500):
    theta = np.linspace(0, 2 * np.pi, n_points)
    radii = np.linspace(radius + 0.1, radius * 3, n_radii)
    points = []
    for r in radii:
        points.extend(r * np.exp(1j * theta))
    return np.array(points)

# Построение областей
def plot_transformation():
    e = np.e  # Радиус исходного круга
    z = generate_external_points(e)  # Внешность круга

    # Этап 1
    z1 = step1(z, e)

    # Этап 2
    z2 = step2(z1)

    # Этап 3
    w = step3(z2)

    # Визуализация
    plt.figure(figsize=(18, 6))

    # Исходная область
    plt.subplot(1, 3, 1)
    plt.scatter(z.real, z.imag, s=1, color='blue', label="Внешность круга |z| ≥ e")
    plt.title("Этап 1: Внешность круга")
    plt.xlabel("Re(z)")
    plt.ylabel("Im(z)")
    plt.axhline(0, color='black', linewidth=0.5)
    plt.axvline(0, color='black', linewidth=0.5)
    plt.gca().set_aspect('equal', adjustable='box')
    plt.legend()

    # После преобразования в полосу
    plt.subplot(1, 3, 2)
    plt.scatter(z2.real, z2.imag, s=1, color='green', label="Полоса")
    plt.title("Этап 2: Преобразование в полосу")
    plt.xlabel("Re(z)")
    plt.ylabel("Im(z)")
    plt.axhline(0, color='black', linewidth=0.5)
    plt.axvline(0, color='black', linewidth=0.5)
    plt.gca().set_aspect('equal', adjustable='box')
    plt.legend()

    # Финальная область
    plt.subplot(1, 3, 3)
    plt.scatter(w.real, w.imag, s=1, color='red', label="Область с вырезами")
    plt.title("Этап 3: Верхняя полуплоскость с вырезами")
    plt.xlabel("Re(w)")
    plt.ylabel("Im(w)")
    plt.axhline(0, color='black', linewidth=0.5)
    plt.axvline(0, color='black', linewidth=0.5)
    plt.gca().set_aspect('equal', adjustable='box')
    plt.legend()

    plt.tight_layout()
    plt.show()

# Запуск программы
plot_transformation()
