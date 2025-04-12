def big_to_little_endian(n):
    """Convert a 32-bit integer from big-endian to little-endian format"""
    return int.from_bytes(n.to_bytes(4, byteorder="big"), byteorder="little")


assert big_to_little_endian(2018915346) == 305419896
assert big_to_little_endian(3721182122) == 2864434397
