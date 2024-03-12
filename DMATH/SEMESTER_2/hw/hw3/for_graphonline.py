# если нужно преобразовать граф для сайта graphonline
matrix = [[1 if c.isdigit() and c != '0'
           else 0 for c in line.split(';')]
          for line in open('graph.csv').read().strip().split('\n')]
for line in matrix:
    print(','.join(map(str, line)))