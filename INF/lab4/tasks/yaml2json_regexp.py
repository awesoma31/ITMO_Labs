from main import IN_YAML, OUT_YAML_REGEXP
import re


def yml2json_regexp(input_file, output_file):
    with open(input_file, 'r', encoding='utf8') as in_file:
        data = in_file.readlines()

    day_pattern = r'Суббота:\n'
    day_repl = r'{\n"Суббота":\n{\n'
    days = []
    for i in range(len(data)):
        if data[i] in ["Суббота:\n"]:
            a = re.sub(day_pattern, day_repl, data[i])
            print(a)


yml2json_regexp(r"C:\Users\gwert\Documents\ITMO_Labs\INF\lab4\tasks\data\init_yaml.yml", OUT_YAML_REGEXP)
