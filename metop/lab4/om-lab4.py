import numpy as np


def coordinate_descent(epsilon=0.0001, max_iter=100):
    x1, x2 = 2, -2
    iter_num = 0

    print(f"Iteration {iter_num}: x1 = {x1:.6f}, x2 = {x2:.6f}, f = {f(x1, x2):.6f}")

    while iter_num < max_iter:
        x1_old, x2_old = x1, x2

        x1 = (3 - 0.5 * x2) / 14.0

        x2 = (5 - 0.5 * x1) / 6.0

        iter_num += 1
        diff = abs(x1 - x1_old) + abs(x2 - x2_old)

        print(
            f"Iteration {iter_num}: x1 = {x1:.6f}, x2 = {x2:.6f}, f = {f(x1, x2):.6f}, diff = {diff:.6f}"
        )

        if diff < epsilon:
            break

    return x1, x2, iter_num


def f(x1, x2):
    return 7 * x1**2 + 3 * x2**2 + 0.5 * x1 * x2 - 3 * x1 - 5 * x2 + 2


if __name__ == "__main__":
    final_x1, final_x2, iterations = coordinate_descent()
    print("\nFinal result:")
    print(f"x1 = {final_x1:.6f}")
    print(f"x2 = {final_x2:.6f}")
    print(f"f(x1, x2) = {f(final_x1, final_x2):.6f}")
    print(f"Total iterations: {iterations}")


def function_value(x1, x2):
    return 7 * x1**2 + 3 * x2**2 + 0.5 * x1 * x2 - 3 * x1 - 5 * x2 + 2


def gradient(x1, x2):
    grad_x1 = 14 * x1 + 0.5 * x2 - 3
    grad_x2 = 6 * x2 + 0.5 * x1 - 5
    return np.array([grad_x1, grad_x2])


def gradient_descent(eta, epsilon, max_iterations):
    x = np.array([2.0, -2.0])
    print(f"Начальная точка: ({x[0]}, {x[1]})")

    min_eta = 1e-6

    for i in range(max_iterations):
        grad = gradient(x[0], x[1])

        if np.linalg.norm(grad) < epsilon:
            print(f"Достигнут минимум после {i+1} итераций.")
            break

        current_val = function_value(x[0], x[1])
        new_x = x - eta * grad
        new_val = function_value(new_x[0], new_x[1])

        retry_count = 0
        while new_val > current_val and eta > min_eta:
            eta /= 2
            retry_count += 1
            new_x = x - eta * grad
            new_val = function_value(new_x[0], new_x[1])

        if eta <= min_eta:
            print(f"Слишком маленький шаг (eta={eta}). Остановка алгоритма.")
            break

        x = new_x
        print(
            f"Итерация {i+1}: точка ({x[0]:.6f}, {x[1]:.6f}), значение функции: {new_val:.6f}, текущий шаг: {eta:.6f}"
        )

    else:
        print("Достигнуто максимальное количество итераций.")

    return x[0], x[1]


# Параметры
eta = 0.1
epsilon = 0.0001
max_iterations = 1000

final_x1, final_x2 = gradient_descent(eta, epsilon, max_iterations)
print(f"Финальная точка: ({final_x1:.6f}, {final_x2:.6f})")
print(f"Значение функции в минимуме: {function_value(final_x1, final_x2):.6f}")


def f(x):
    x1, x2 = x
    return 7 * x1**2 + 3 * x2**2 + 0.5 * x1 * x2 - 3 * x1 - 5 * x2 + 2


def gradient(x):
    x1, x2 = x
    grad_x1 = 14 * x1 + 0.5 * x2 - 3
    grad_x2 = 6 * x2 + 0.5 * x1 - 5
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


x0 = [2.0, -2.0]
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
