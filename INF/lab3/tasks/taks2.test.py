import task2
import unittest


class TestTask1(unittest.TestCase):
    def test_example(self):
        data = 'А ты знал, что ВТ – лучшая кафедра в ИТМО?'
        result = 'ВТ лучшая кафедра в ИТМО'
        self.assertEqual(result, task2.solve(data))


if __name__ == '__main__':
    unittest.main()
