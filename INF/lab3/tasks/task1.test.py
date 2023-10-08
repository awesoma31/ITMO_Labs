import task1
import unittest


class TestTask1(unittest.TestCase):
    def test_no_emotions(self):
        data = '23o4n3nff-/dsPPPX-sosd'
        result = 0
        self.assertEqual(result, task1.solve(data))

    def test_one_emotions(self):
        data = 'X-P x-P'
        result = 1
        self.assertEqual(result, task1.solve(data))

    def test_many_emotions(self):
        data = 'X-P abcdefX-Pasfs X - P X- P'
        result = 2
        self.assertEqual(result, task1.solve(data))

    def test_emotions_in_a_row(self):
        data = 'X-PX-PX-Px-p ----X-P----'
        result = 4
        self.assertEqual(result, task1.solve(data))

    def test_broken_emotions(self):
        data = 'X - P x- P  X-p x_p X09-12P'
        result = 0
        self.assertEqual(result, task1.solve(data))


if __name__ == '__main__':
    unittest.main()
