import time


def yaml2json(input_file, output_file):
    with open(input_file, 'r', encoding='utf8') as in_file:
        data = in_file.readlines()
        numb_lines = len(data)
    # print(data)

    out_file = open(output_file, 'w')
    out_file.write("{\n")

    lst = [
        "Суббота:\n", " Расписание:\n", "  Пара1:\n", "  Пара2:\n", "  Пара3:\n", "  Пара4:\n", "  Пара5:\n",
        "  Пара6:\n", "  Пара7:\n", "  Пара8:\n"
    ]

    for i in range(0, numb_lines - 1):
        if data[i] in ["Суббота:\n"]:
            if data[0] == data[i]:
                sup_string = data[i].lstrip().split(':', maxsplit=1)
                out_file.write('"' + sup_string[0] + '":' + sup_string[1])
            else:
                sup_string = data[i].lstrip().split(':', maxsplit=1)
                out_file.write(' }\n }\n\n"' + sup_string[0] + '":' + sup_string[1])

        elif data[i] in [" Расписание:\n"]:
            sup_string = data[i].lstrip().split(':', maxsplit=1)
            out_file.write('\t{\n')
            out_file.write('\t"' + sup_string[0] + '":' + sup_string[1])
            out_file.write('\t\t{\n')

        elif data[i] in ["  Пара1:\n", "  Пара2:\n", "  Пара3:\n", "  Пара4:\n", "  Пара5:\n", "  Пара6:\n",
                         "  Пара7:\n", "  Пара8:\n"]:
            sup_string = data[i].lstrip().split(':', maxsplit=1)
            out_file.write('\t\t"' + sup_string[0] + '":' + sup_string[1])
            out_file.write('\t\t\t{\n')

        else:
            if data[i + 1] in lst or i + 1 == numb_lines:
                sup_string = data[i].lstrip().split(':', maxsplit=1)
                a = sup_string[1].split("\n")
                out_file.write('\t\t\t\t"' + sup_string[0] + '":' + a[0].lstrip() + "\n")
            else:
                sup_string = data[i].lstrip().split(':', maxsplit=1)
                a = sup_string[1].split("\n")
                out_file.write('\t\t\t\t"' + sup_string[0] + '":' + a[0].lstrip() + ",\n")
            if data[i + 1] in lst:
                out_file.write('\t\t\t},\n')

    out_file.write("\t\t\t}\n\t\t}\n\t}\n}"'\n')


IN_YAML = r"C:\Users\gwert\Documents\ITMO_Labs\INF\lab4\tasks\data\init_yaml.yml"

st = time.time()
for i in range(100):
    yaml2json(
        IN_YAML,
        r"C:\Users\gwert\Documents\ITMO_Labs\INF\lab4\tasks\data\end_json.json"
    )
et = time.time()
ext = et - st
print("Время выполнения, используя свой парсер - " + str(ext))
