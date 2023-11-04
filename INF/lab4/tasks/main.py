import time


def yaml2json(input_file, output_file):
    with open(input_file, 'r', encoding='utf8') as in_file:
        data = in_file.readlines()
        numb_lines = len(data)

    out_file = open(output_file, 'w', encoding='utf8')
    out_file.write("{\n")

    lst = [
        "Суббота:\n", " Расписание:\n", "  Пара1:\n", "  Пара2:\n", "  Пара3:\n", "  Пара4:\n", "  Пара5:\n",
        "  Пара6:\n", "  Пара7:\n", "  Пара8:\n"
    ]

    for i in range(0, numb_lines):
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
            if i + 1 == numb_lines:
                sup_string = data[i].lstrip().split(':', maxsplit=1)
                a = sup_string[1].split("\n")
                out_file.write('\t\t\t\t"' + sup_string[0] + '":' + a[0].lstrip() + "\n")
            elif i + 1 != numb_lines and (data[i + 1] in lst):
                sup_string = data[i].lstrip().split(':', maxsplit=1)
                a = sup_string[1].split("\n")
                out_file.write('\t\t\t\t"' + sup_string[0] + '":' + a[0].lstrip() + "\n")
            else:
                sup_string = data[i].lstrip().split(':', maxsplit=1)
                a = sup_string[1].split("\n")
                out_file.write('\t\t\t\t"' + sup_string[0] + '":' + a[0].lstrip() + ",\n")
            if i + 1 != numb_lines and data[i + 1] in lst:
                out_file.write('\t\t\t},\n')

    out_file.write("\t\t\t}\n\t\t}\n\t}\n}"'\n')
    out_file.close()


IN_YAML = r"data\init_yaml.yml"
OUT_JSON = r"data\end_json.json"

st = time.time()
for i in range(100):
    yaml2json(IN_YAML, OUT_JSON)
et = time.time()
ext = et - st
print("Время выполнения, используя свой парсер - " + str(ext))
