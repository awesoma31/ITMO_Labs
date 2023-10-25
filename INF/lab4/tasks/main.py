def yaml2json(input_file, output_file):
    data = []
    numb_lines = 0
    close_think = []
    sup_string = []
    with open(input_file, 'r', encoding='utf8') as in_file:
        new_line = in_file.readline()
        while new_line:
            data.append(new_line)
            numb_lines += 1
            new_line = in_file.readline()

    start_k = len(data[0]) - len(data[0].lstrip())
    out_file = open(output_file, 'w')
    out_file.write("{\n")
    print(data)
    lst = ["Суббота:\n", " Расписание:\n", "  Пара1:\n", "  Пара2:\n", "  Пара3:\n", "  Пара4:\n", "  Пара5:\n",
           "  Пара6:\n",
           "  Пара7:\n", "  Пара8:\n"]
    for i in range(0, numb_lines - 1):
        end_k = len(data[i + 1]) - len(data[i + 1].lstrip())

        # преобразовать день недели
        if data[i] in ["Суббота:\n"]:
            if data[0] == data[i]:
                sup_string = data[i].lstrip().split(':', maxsplit=1)
                out_file.write('"' + sup_string[0] + '":' + sup_string[1])
            else:
                sup_string = data[i].lstrip().split(':', maxsplit=1)
                out_file.write(' }\n }\n\n"' + sup_string[0] + '":' + sup_string[1])

            # зайти под уровень субботы обраюотать расписание
            # for j in range(i, numb_lines - 1):

                # # preobr raspisani
                # if data[j] in [" Расписание:\n"]:
                #     sup_string = data[j].lstrip().split(':', maxsplit=1)
                #     # print(sup_string)
                #     out_file.write('\t{\n')
                #     out_file.write('\t"' + sup_string[0] + '":' + sup_string[1])
                #     out_file.write('\t\t{\n')
                #
                # # зайти в подуровень обработать пары
                # for k in range(j, numb_lines - 1):
                #     if data[k] in ["  Пара1:\n", "  Пара2:\n", "  Пара3:\n", "  Пара4:\n", "  Пара5:\n", "  Пара6:\n",
                #                    "  Пара7:\n", "  Пара8:\n"]:
                #         sup_string = data[k].lstrip().split(':', maxsplit=1)
                #         out_file.write('\t\t"' + sup_string[0] + '":' + sup_string[1])
                #         out_file.write('\t\t\t{\n')
                #     pass

        # преобр расписание
        elif data[i] in [" Расписание:\n"]:
            sup_string = data[i].lstrip().split(':', maxsplit=1)
            out_file.write('\t{\n')
            out_file.write('\t"' + sup_string[0] + '":' + sup_string[1])
            out_file.write('\t\t{\n')

        # преобр параномер
        elif data[i] in ["  Пара1:\n", "  Пара2:\n", "  Пара3:\n", "  Пара4:\n", "  Пара5:\n", "  Пара6:\n",
                         "  Пара7:\n", "  Пара8:\n"]:
            sup_string = data[i].lstrip().split(':', maxsplit=1)
            out_file.write('\t\t"' + sup_string[0] + '":' + sup_string[1])
            out_file.write('\t\t\t{\n')


        else:
            if data[i+1] in ["  Пара1:\n", "  Пара2:\n", "  Пара3:\n", "  Пара4:\n", "  Пара5:\n", "  Пара6:\n",
                         "  Пара7:\n", "  Пара8:\n"] or i+1 == numb_lines:
                sup_string = data[i].lstrip().split(':', maxsplit=1)
                a = sup_string[1].split("\n")
                out_file.write('\t\t\t\t"' + sup_string[0] + '":' + a[0].lstrip() + "\n")
            else:
                sup_string = data[i].lstrip().split(':', maxsplit=1)
                a = sup_string[1].split("\n")
                out_file.write('\t\t\t\t"' + sup_string[0] + '":' + a[0].lstrip() + ",\n")
            if data[i+1] in lst:
                out_file.write('\t\t\t},\n')

        # if end_k < start_k:
    out_file.write("\t\t\t}\n\t\t}\n\t}\n}"'\n')


# if __name__ == '__main__':
yaml2json(r"C:\Users\gwert\Documents\ITMO_Labs\INF\lab4\tasks\data\init_yaml.yml", r"end_json.json")
# get_yaml('https://itmo.ru/ru/schedule/3/125598/raspisanie_zanyatiy.htm#6day', 'yaml.yml')
