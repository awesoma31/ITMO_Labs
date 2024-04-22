import unittest
import task2


class TestTask2(unittest.TestCase):
    def test_example(self):
        data = 'А ты знал, что ВТ – лучшая кафедра в ИТМО?'
        result = ['ВТ – лучшая кафедра в ИТМО']
        self.assertEqual(result, task2.solve(data))

    def test_no_vt(self):
        data = 'ИТМО один уз лучших ВУЗов России'
        result = []
        self.assertEqual(result, task2.solve(data))

    def test_no_words(self):
        data = 'ВТ ИТМО'
        result = ['ВТ ИТМО']
        self.assertEqual(result, task2.solve(data))

    def test_one_word(self):
        data = 'ВТ и ИТМО'
        result = ['ВТ и ИТМО']
        self.assertEqual(result, task2.solve(data))

    def test_many_words(self):
        data = 'ВТ и а о ром пом пом ИТМО'
        result = []
        self.assertEqual(result, task2.solve(data))


if __name__ == '__main__':
    unittest.main()
