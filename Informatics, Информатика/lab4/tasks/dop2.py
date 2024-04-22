import re
import time


def yml2json_regexp(input_file, out_f):
    with open(input_file, 'r', encoding='utf8') as in_file:
        data = in_file.readlines()

    day_pattern = r'Суббота:\n'
    day_repl = r'{\n"Суббота":\n'

    lst = [
        "Суббота:\n", " Расписание:\n", "  Пара1:\n", "  Пара2:\n", "  Пара3:\n", "  Пара4:\n", "  Пара5:\n",
        "  Пара6:\n", "  Пара7:\n", "  Пара8:\n"
    ]

    with open(out_f, 'w', encoding='utf8') as out_f:
        for i in range(len(data)):
            if data[i] in ["Суббота:\n"]:
                rpl = re.sub(day_pattern, day_repl, data[i])
                out_f.write(rpl)

            elif data[i] in [" Расписание:\n"]:
                rpl = re.sub(" Расписание:\n", '\t{\n\t"Расписание":\n\t\t{\n', data[i])
                out_f.write(rpl)

            elif data[i] in ["  Пара1:\n", "  Пара2:\n", "  Пара3:\n", "  Пара4:\n", "  Пара5:\n", "  Пара6:\n",
                             "  Пара7:\n", "  Пара8:\n"]:
                sup_string = data[i].lstrip().split(':', maxsplit=1)
                out_f.write('\t\t"' + sup_string[0] + '":' + sup_string[1])
                out_f.write('\t\t\t{\n')

            else:
                if i + 1 == len(data):
                    sup_string = data[i].lstrip().split(':', maxsplit=1)
                    a = sup_string[1].split("\n")
                    out_f.write('\t\t\t\t"' + sup_string[0] + '":' + a[0].lstrip() + "\n")
                elif i + 1 != len(data) and (data[i + 1] in lst):
                    sup_string = data[i].lstrip().split(':', maxsplit=1)
                    a = sup_string[1].split("\n")
                    out_f.write('\t\t\t\t"' + sup_string[0] + '":' + a[0].lstrip() + "\n")
                else:
                    sup_string = data[i].lstrip().split(':', maxsplit=1)
                    a = sup_string[1].split("\n")
                    out_f.write('\t\t\t\t"' + sup_string[0] + '":' + a[0].lstrip() + ",\n")
                if i + 1 != len(data) and data[i + 1] in lst:
                    out_f.write('\t\t\t},\n')

        out_f.write("\t\t\t}\n\t\t}\n\t}\n}"'\n')


IN_YAML = r"data\init_yaml.yml"
OUT_JSON_REGEXP = r"data\regexp.json"

st = time.time()
for i in range(100):
    yml2json_regexp(IN_YAML, OUT_JSON_REGEXP)

et = time.time()
tm = et - st

print("Время выполнения, используя регулярки - " + str(tm))
