import time

import yaml
import json
from main import IN_YAML, OUT_YAML_LIB
st = time.time()

for i in range(100):
    with open(IN_YAML, 'r', encoding='utf8') as yaml_in, open(OUT_YAML_LIB, "w") as json_out:
        yaml_object = yaml.safe_load(yaml_in)  # yaml_object will be a list or a dict
        json.dump(yaml_object, json_out)

et = time.time()
ext = et - st
print(ext)
