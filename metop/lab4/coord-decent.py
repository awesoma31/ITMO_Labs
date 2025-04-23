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
    # return 7 * x1**2 + 3 * x2**2 + 0.5 * x1 * x2 - 3 * x1 - 5 * x2 + 2
    return 2 * x1**2 + 4 * x2**2 - 5 * x1 * x2 + 11 * x1 + 8 * x2 - 3


if __name__ == "__main__":
    final_x1, final_x2, iterations = coordinate_descent()
    print("\nFinal result:")
    print(f"x1 = {final_x1:.6f}")
    print(f"x2 = {final_x2:.6f}")
    print(f"f(x1, x2) = {f(final_x1, final_x2):.6f}")
    print(f"Total iterations: {iterations}")
