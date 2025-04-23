import numpy as np


def function_value(x1, x2):
    return 2 * x1**2 + 4 * x2**2 - 5 * x1 * x2 + 11 * x1 + 8 * x2 - 3


def gradient(x1, x2):
    grad_x1 = 4 * x1 - 5 * x2 + 11
    grad_x2 = 8 * x2 - 5 * x1 + 8
    return np.array([grad_x1, grad_x2])


def gradient_descent(_lambda, epsilon, max_iterations):
    x = np.array([1.0, 1.0])
    print(f"Начальная точка: ({x[0]}, {x[1]})")

    min__lambda = 1e-4

    for i in range(max_iterations):
        grad = gradient(x[0], x[1])

        if np.linalg.norm(grad) < epsilon:
            print(f"Достигнут минимум после {i+1} итераций.")
            break

        current_val = function_value(x[0], x[1])
        new_x = x - _lambda * grad
        new_val = function_value(new_x[0], new_x[1])

        retry_count = 0
        while new_val > current_val and _lambda > min__lambda:
            _lambda /= 2  # уменьшаем шаг
            retry_count += 1
            new_x = x - _lambda * grad
            new_val = function_value(new_x[0], new_x[1])

        if _lambda <= min__lambda:
            print(f"Слишком маленький шаг (_lambda={_lambda}). Остановка алгоритма.")
            break

        x = new_x
        print(
            f"Итерация {i+1}: точка ({x[0]:.6f}, {x[1]:.6f}), значение функции: {new_val:.6f}, текущий шаг: {_lambda:.6f}"
        )

    else:
        print("Достигнуто максимальное количество итераций.")

    return x[0], x[1]


# Параметры
_lambda = 0.1
epsilon = 0.0001
max_iterations = 1000

final_x1, final_x2 = gradient_descent(_lambda, epsilon, max_iterations)
print(f"Финальная точка: ({final_x1:.6f}, {final_x2:.6f})")
print(f"Значение функции в минимуме: {function_value(final_x1, final_x2):.6f}")
