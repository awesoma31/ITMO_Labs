import re


def solve(string):
    """
    Дан текст. Необходимо найти в нём каждый фрагмент, где сначала идёт слово «ВТ»,
    затем не более 4 слов, и после этого идёт слово «ИТМО».
    Для простоты будем считать словом любую последовательность букв, цифр и знаков «_» (то есть символов \w).

    :param string:
    :return: res: list of fragments found in the text
    """
    pattern = re.compile(r"ВТ(?:\W*[\s-]+\w+){0,4}[\s-]+ИТМО")
    res = pattern.findall(string)

    return res

