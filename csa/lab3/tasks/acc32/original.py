def sum_word_cstream(*xs):
    """Input: stream of word (32 bit) in c string style (end with 0).

    Need to sum all numbers and send result in two words (64 bits).
    """
    tmp = 0
    x = 0
    for x in xs:
        if x == 0:
            break
        tmp += x
    assert x == 0
    hw, lw = ((tmp & 0xFFFF_FFFF_0000_0000) >> 32), tmp & 0x0000_0000_FFFF_FFFF
    return [hw, lw]


assert sum_word_cstream([48, 18, 0]) == [0, 66]
assert sum_word_cstream([1, 0]) == [0, 1]
assert sum_word_cstream([1, 0]) == [0, 1]
assert sum_word_cstream([2147483647, 1, 0]) == [0, 2147483648]
assert sum_word_cstream([2147483647, 1, 2147483647, 0]) == [0, 4294967295]
assert sum_word_cstream([2147483647, 1, 2147483647, 1, 0]) == [1, 0]
assert sum_word_cstream([2147483647, 1, 2147483647, 2, 0]) == [1, 1]
