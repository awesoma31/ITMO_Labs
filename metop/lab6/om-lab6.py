import random

import numpy as np

# --------------------------- CONSTANTS ---------------------------------------
MUTATION_RATE = 0.01
NUM_CITIES = 5
POP_SIZE = 4
NUM_GENERATIONS = 3

# Distance matrix between the 5 cities
DIST = np.array(
    [
        [0, 4, 6, 2, 9],
        [4, 0, 3, 2, 9],
        [6, 3, 0, 5, 9],
        [2, 2, 5, 0, 8],
        [9, 9, 9, 8, 0],
    ]
)


# --------------------------------------------------------------------------- #
def path_len(route: list[int]) -> int:
    """
    Compute total tour length (objective value).

    This sums the distances for each consecutive edge in the permutation and
    finally adds the edge that closes the tour (last city → first city).
    """
    return sum(DIST[route[i], route[(i + 1) % NUM_CITIES]] for i in range(NUM_CITIES))


# --------------------------------------------------------------------------- #
def init_population() -> list[list[int]]:
    """Create initial population of random permutations."""
    return [random.sample(range(NUM_CITIES), NUM_CITIES) for _ in range(POP_SIZE)]


# --------------------------------------------------------------------------- #
def mutate(route: list[int]) -> bool:
    """Swap-mutation: exchange two random positions (returns True if mutated)."""
    if random.random() < MUTATION_RATE:
        i, j = random.sample(range(NUM_CITIES), 2)
        route[i], route[j] = route[j], route[i]
        return True
    return False


# -------- Order-Crossover with fragment exchange and “left-fill” ------------ #
def ox(parent_host: list[int], parent_donor: list[int], a: int, b: int) -> list[int]:
    """
    1. Exchange step: copy slice parent_donor[a:b] into child
       (this is where offspring inherit the foreign fragment).
    2. Filling step: walk through parent_host (starting at a+1, cyclic)
       and insert missing cities into the LEFT-most free “stars”,
       then continue on the right side of the exchanged slice.

    • The fragment exchange happens once at line 'child[a:b+1] = ...'.
    • The subsequent loop fills remaining gaps preserving order
      and ensuring no duplicates.
    """
    n = len(parent_host)
    child = [-1] * n  # empty child
    child[a : b + 1] = parent_donor[a : b + 1]  # *** EXCHANGE SEGMENT ***

    # insertion order: all indices left of slice, then right of slice
    positions = list(range(0, a)) + list(range(b + 1, n))

    idx_host = (a + 1) % n  # start scanning host
    for _ in range(n):
        gene = parent_host[idx_host]
        if gene not in child:
            pos = positions.pop(0)  # *** FILL FIRST LEFT STAR ***
            child[pos] = gene
        idx_host = (idx_host + 1) % n
    return child


# ------------------------------ GA MAIN LOOP -------------------------------- #
def genetic_algorithm():
    population = init_population()

    for gen in range(NUM_GENERATIONS):
        fitness = np.array([path_len(r) for r in population])
        probs = 1 / fitness
        probs /= probs.sum()  # better tour gets more chance to be the parent

        print(f"\nGeneration {gen + 1}:")
        print("Population:", population)
        print("Distances:", fitness)

        new_population = []
        pair_no = 0
        while len(new_population) < POP_SIZE:
            i1, i2 = np.random.choice(len(population), 2, replace=False, p=probs)
            p1, p2 = population[i1], population[i2]
            pair_no += 1

            # inner crossover points (no edge positions)
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

        # Elitism: join old + new individuals and keep the best 4 tours
        # (lowest objective value) to form the next generation.
        population = sorted(population + new_population, key=path_len)[:POP_SIZE]

        print("\nEnlarged population:", population)
        print("Distances:", [path_len(r) for r in population])

    best = min(population, key=path_len)
    return best, path_len(best)


# --------------------------------------------------------------------------- #
if __name__ == "__main__":
    best_route, best_dist = genetic_algorithm()
    print(f"\nBest route: {best_route}, Distance: {best_dist}")
