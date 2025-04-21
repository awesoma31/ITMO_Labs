#include <cstddef>
#include <iostream>
#include <limits>

auto main() -> int {
  size_t a{};
  size_t b{};
  size_t c{};
  size_t d{};
  size_t k{};

  std::cin >> a >> b >> c >> d >> k;

  size_t prev = std::numeric_limits<size_t>::max();
  size_t current_count = a;

  while ((k--) != 0) {
    current_count *= b;
    if (current_count < c) {
      current_count = 0;
      break;
    }
    current_count -= c;
    current_count = std::min(current_count, d);
    if (current_count == prev) {
      break;
    }
    prev = current_count;
  }

  std::cout << current_count << '\n';
}
