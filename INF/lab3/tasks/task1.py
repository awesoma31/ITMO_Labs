import re


def solve(string):
    """
    Возвращает кол-во смайликов вида X-P в строке string
    409856 % 6 = 2 => Глаза: X
    409856 % 4 = 0 => Нос: -
    409856 % 7 = 6 => Рот: P
    :param string
    :return: int: number of emotions found in the text
    """
    pattern = r'X-P'
    return len(re.findall(pattern, string))
