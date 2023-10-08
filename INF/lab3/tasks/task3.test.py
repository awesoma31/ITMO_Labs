import unittest
import task3


class TestTask3(unittest.TestCase):
    def test_no_vowels(self):
        data = 'йй ццц ккк НРНН ггг'
        result = []
        self.assertEqual(result, task3.solve(data))

    def test_example(self):
        data = 'Классное слово – обороноспособность, которое должно идти после слов: трава и молоко.'
        result = ['и', 'идти', 'слов', 'слово', 'трава', 'должно', 'молоко', 'обороноспособность']
        self.assertEqual(result, task3.solve(data))

    def test_duplicates(self):
        data = 'пип пип бумм бумм'
        result = ['пип', 'бумм']
        self.assertEqual(result, task3.solve(data))

    def test_only_vowels(self):
        data = 'о а оо ааа уу ооо'
        result = ['а', 'о', 'оо', 'уу', 'ааа', 'ооо']
        self.assertEqual(result, task3.solve(data))

    def test_sentence(self):
        data = 'Кеннет Элтон «Кен» Кизи — американский писатель, драматург, журналист.'
        result = ['Кен', 'Кизи', 'Кеннет']
        self.assertEqual(result, task3.solve(data))


if __name__ == '__main__':
    unittest.main()
