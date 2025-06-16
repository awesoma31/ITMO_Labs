# по матрице смежности при помощи алгоритма Франка-Фриша
# определяет (s-t) путь с наибольшей пропускной способностью
# и выводит решение по шагам

matrix = [[int(c) if c.isdigit() and c != '0'
           else 0 for c in line.split(';')]
          for line in open('graph.csv').read().strip().split('\n')]
graph = {v + 1: {u + 1: matrix[v][u] for u in range(len(matrix[v]))}
         for v in range(len(matrix))}
graph_new_graph = {k: {k, } for k in graph}
# каждой вершине соответствует набор вершин после укорочения рёбер
s = 1
t = 12
print(f's={s} t={t}')
i = 1
Q = float('inf')
while not({s, t} <= graph_new_graph[s]):
    q = 0
    for v in graph_new_graph[s]:
        for u in graph:
            if u not in graph_new_graph[s] and graph[v][u]:
                q = max(q, graph[u][v])
    Q = min(Q, q)
    print(f'i={i}, Qi={q}')
    edges = []  # рёбра, которые мы будем закорачивать
    for v in graph:
        for u in graph:
            # если вершины ещё не лежат в одном наборе (ребро на закорочено)
            if not({v, u} <= graph_new_graph[v]):
                if graph[v][u] >= q and (u, v) not in edges:
                    edges.append((v, u))
    print('Закорачиваем рёбра:')
    print(edges)
    for v, u in edges:
        new_set = graph_new_graph[v] | graph_new_graph[u]
        for u1 in graph_new_graph[u]:
            graph_new_graph[u1] = new_set
        for v1 in graph_new_graph[v]:
            graph_new_graph[v1] = new_set
    i += 1
    print(graph_new_graph)
    print('-' * 30)
print('Ответ:')
print(f'Q={Q}')