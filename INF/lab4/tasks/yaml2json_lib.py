import json
import time
import yaml

IN_YAML = r"C:\Users\gwert\Documents\ITMO_Labs\INF\lab4\tasks\data\init_yaml.yml"
OUT_YAML_LIB = r"C:\Users\gwert\Documents\ITMO_Labs\INF\lab4\tasks\data\json_lib.json"

st = time.time()

for i in range(100):
    with open(IN_YAML, 'r', encoding='utf8') as yaml_in, open(OUT_YAML_LIB, "w") as json_out:
        yaml_object = yaml.safe_load(yaml_in)
        json.dump(yaml_object, json_out)

et = time.time()
ext = et - st

print("Время выполнения, используя библиотеки - " + str(ext))
