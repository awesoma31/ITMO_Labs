import random
import numpy as np

# --------------------------- ПАРАМЕТРЫ ---------------------------------------
MUTATION_RATE = 0.01
NUM_CITIES = 5
POP_SIZE = 4
NUM_GENERATIONS = 3

DIST = np.array(
    [
        [0, 4, 6, 2, 9],
        [4, 0, 3, 2, 9],
        [6, 3, 0, 5, 9],
        [2, 2, 5, 0, 8],
        [9, 9, 9, 8, 0],
    ]
)


def path_len(route):
    """Сумма рёбер + возврат в стартовый город."""
    return sum(DIST[route[i], route[(i + 1) % NUM_CITIES]] for i in range(NUM_CITIES))


def init_population():
    return [random.sample(range(NUM_CITIES), NUM_CITIES) for _ in range(POP_SIZE)]


def mutate(route):
    """Обмен двух случайных генов; True, если была мутация."""
    if random.random() < MUTATION_RATE:
        i, j = random.sample(range(NUM_CITIES), 2)
        route[i], route[j] = route[j], route[i]
        return True
    return False


# -------- Order‑Crossover с обменом фрагментов и «левым» заполнением ----------
def ox(parent_host, parent_donor, a, b):
    """
    1. Вставляем фрагмент parent_donor[a:b] в ребёнка.
    2. Обходим parent_host, начиная с позиции (a+1) по кругу,
       и последовательно вставляем недостающие города
       в ЛЕВУЮ незаполненную ячейку, затем продолжаем справа от фрагмента.
    """
    n = len(parent_host)
    child = [-1] * n  # шаг 0: пустая особь
    child[a : b + 1] = parent_donor[a : b + 1]  # шаг 1: «чужой» фрагмент

    # порядок вставки позиций: сначала слева (0..a‑1), потом справа (b+1..n‑1)
    positions = list(range(0, a)) + list(range(b + 1, n))

    # начинаем обход host со второго элемента выделенного фрагмента (a+1)
    idx_host = (a + 1) % n
    for _ in range(n):  # максимум n проверок
        gene = parent_host[idx_host]
        if gene not in child:  # ещё не вставлен?
            pos = positions.pop(0)  # берём самую левую свободную «звёздочку»
            child[pos] = gene
        idx_host = (idx_host + 1) % n
    return child


# --------------------------- ГЕНЕТИЧЕСКИЙ ЦИКЛ --------------------------------
def genetic_algorithm():
    population = init_population()

    for gen in range(NUM_GENERATIONS):
        fitness = np.array([path_len(r) for r in population])
        probs = 1 / fitness
        probs /= probs.sum()

        print(f"\nGeneration {gen + 1}:")
        print("Population:", population)
        print("Distances:", fitness)

        new_population = []
        pair_no = 0
        while len(new_population) < POP_SIZE:
            i1, i2 = np.random.choice(len(population), 2, replace=False, p=probs)
            p1, p2 = population[i1], population[i2]
            pair_no += 1

            # точки разрыва обязательно «внутренние»: 1 .. n‑2
            a, b = sorted(random.sample(range(1, NUM_CITIES - 1), 2))

            print(f"\nPair {pair_no}: [{i1}, {i2}]  cuts {a},{b}")
            print("Parent 1:", *p1[:a], "|", *p1[a : b + 1], "|", *p1[b + 1 :])
            print("Parent 2:", *p2[:a], "|", *p2[a : b + 1], "|", *p2[b + 1 :])

            child1 = ox(p1, p2, a, b)
            child2 = ox(p2, p1, a, b)
            print("Child 1:", child1)
            print("Child 2:", child2)

            if mutate(child1):
                print("Child 1 MUTATED:", child1)
            if mutate(child2):
                print("Child 2 MUTATED:", child2)

            new_population.extend([child1, child2])

        # элитизм: отбираем лучшие POP_SIZE
        population = sorted(population + new_population, key=path_len)[:POP_SIZE]
        print("\nEnlarged population:", population)
        print("Distances:", [path_len(r) for r in population])

    best = min(population, key=path_len)
    return best, path_len(best)


if __name__ == "__main__":
    best_route, best_dist = genetic_algorithm()
    print(f"\nBest route: {best_route}, Distance: {best_dist}")
