# по матрице смежности при помощи алгоритма Дейкстры находит кратчайшие
# пути до всех вершин графа, поясняет каждый шаг

matrix = [[int(c) if c.isdigit() and c != '0'
           else 0 for c in line.split(';')]
          for line in open('graph.csv').read().strip().split('\n')]
graph = {v + 1: {u + 1: matrix[v][u] for u in range(len(matrix[v]))}
         for v in range(len(matrix))}
# graph[v][u] == 1 - смежные вершины, 0 - несмежные
n = len(graph)
l = {v: float('inf') for v in graph}
l_plus = {v: None for v in graph}
p = 1
l[p] = 0
l_plus[p] = l[p]
while None in l_plus.values():
    print(f'p={p}')
    print(f'Гp={",".join(map(str, filter(lambda x: graph[p][x], graph)))}')
    print(f'Временные пометки из них: '
          f'{",".join(map(str, filter(lambda x: graph[p][x] and l_plus[x] is None, graph)))}')
    for u in graph:
        if graph[p][u] != 0 and l_plus[u] is None:
            print(f'l({u})=min({l[u]},{l[p]}+{graph[p][u]})='
                  f'{min(l[u], l[p] + graph[p][u])}')
            l[u] = min(l[u], l[p] + graph[p][u])
    v = min(filter(lambda x: l_plus[x] is None, graph),
            key=lambda x: l[x])
    p = v
    l_plus[p] = l[p]
    print(f'p={p}, l={l[p]}+')
    for u in l:
        if u == p:
            print(f'l({u})={l[u]}+')
        else:
            print(f'l({u})={l[u] if l_plus[u] is None else ""}')
    print('-' * 30)
print('Ответ:')
for v in l_plus:
    print(f'v={v}, l={l_plus[v]}+')