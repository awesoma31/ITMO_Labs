import re


def solve(string):
    """
    Дан текст. Необходимо найти в нём каждый фрагмент, где сначала идёт слово «ВТ»,
    затем не более 4 слов, и после этого идёт слово «ИТМО».
    Для простоты будем считать словом любую последовательность букв, цифр и знаков «_» (то есть символов \w).

    :param string:
    :return: res: list of fragments found in the text
    """
    # TODO: pattern

    pattern = r'ВТ\s\w*\sИТМО'
    res = re.findall(pattern, string)

    return res


# string = 'А ты знал, что ВТ – лучшая кафедра в ИТМО?'
# print(solve('string'))
