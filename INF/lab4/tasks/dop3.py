import time


def count_spaces(row):
    return len(row) - len(row.lstrip())


def my_yaml2json(in_file, out_f):
    res = {}
    with open(in_file, 'r', encoding="utf8") as f:
        data = f.readlines()

    hierarchy = []

    for i in data:
        key, value = i.split(":", 1)
        layer, key = count_spaces(key) // 2, key.lstrip()
        hierarchy.insert(layer, key)

        while hierarchy[-1] != key:
            hierarchy.pop()
        if value == '\n':
            cur_tree = res
            for j in hierarchy:
                if j in cur_tree:
                    cur_tree = cur_tree[j]
                else:
                    cur_tree[j] = {}
        else:
            cur_tree = res
            for j in hierarchy:
                if j in cur_tree:
                    cur_tree = cur_tree[j]
                else:
                    cur_tree[j] = value.strip()

    a = str(res).replace('\'', '\"')
    with open(out_f, 'w', encoding='utf8') as out_f:
        out_f.write(a)


test1file = r"data\test1.yml"
test2file = r"data\test2.yml"
OUT_FILE = r"data\dop3.yml"

st = time.time()

for i in range(100):
    my_yaml2json(test1file, OUT_FILE)

et = time.time()
ext = et - st
print("Время выполнения доп. задания 3 - ", str(ext))
