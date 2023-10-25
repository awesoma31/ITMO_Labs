import requests


def get_yaml(url, out_file):
    response = requests.get(url)

    if response.status_code == 200:
        yaml_content = response.text

        with open(out_file, 'w') as file:
            file.write(yaml_content)

        print(f"YAML-файл успешно загружен и сохранен как {file}")
    else:
        print(f"Не удалось получить YAML-файл. Код состояния: {response.status_code}")
