import numpy as np


def f(x1, x2):
    return 2 * x1**2 + 4 * x2**2 - 5 * x1 * x2 + 11 * x1 + 8 * x2 - 3


def gradient(x1, x2):
    grad_x1 = 4 * x1 - 5 * x2 + 11
    grad_x2 = 8 * x2 - 5 * x1 + 8
    return np.array([grad_x1, grad_x2])


def golden_section_search(f, a, b, tol=1e-5, max_iter=100):
    ratio = (np.sqrt(5) - 1) / 2
    c = b - ratio * (b - a)
    d = a + ratio * (b - a)

    for _ in range(max_iter):
        if f(c) < f(d):
            b = d
        else:
            a = c

        c = b - ratio * (b - a)
        d = a + ratio * (b - a)

        if abs(b - a) < tol:
            break
    return (a + b) / 2


def steepest_descent(f, grad, x0, epsilon=1e-4, max_iter=100):
    x = np.array(x0, dtype=float)
    history = []

    for k in range(max_iter):
        g = grad(x)
        grad_norm = np.linalg.norm(g)
        history.append((x.copy(), f(x), grad_norm))

        if grad_norm < epsilon:
            break

        S = -g / grad_norm

        def f_alpha(alpha):
            return f(x + alpha * S)

        alpha = golden_section_search(f_alpha, 0, 1)
        x = x + alpha * S

    return x, history


x0 = [1.0, 1.0]
epsilon = 0.0001

solution, history = steepest_descent(f, gradient, x0, epsilon)

print("Метод наискорейшего спуска:")
print(f"Начальная точка: ({x0[0]}, {x0[1]})")
print("=============================================")

for i, (x, f_val, grad_norm) in enumerate(history[:3]):
    print(f"Итерация {i+1}:")
    print(f"Точка: ({x[0]:.4f}, {x[1]:.4f})")
    print(f"Значение функции: {f_val:.4f}")
    print(f"Норма градиента: {grad_norm:.4f}")
    print("---------------------------------------------")

final_x, final_f, final_grad_norm = history[-1]
print(f"\nИтоговая точка: ({final_x[0]:.6f}, {final_x[1]:.6f})")
print(f"Значение функции: {final_f:.6f}")
print(f"Норма градиента: {final_grad_norm:.6f}")
if final_grad_norm < epsilon:
    print("Условие остановки выполнено!")
else:
    print("Достигнуто максимальное число итераций.")
