#include <cstddef>
#include <iostream>
#include <list>
#include <optional>
#include <string>
#include <utility>
#include <vector>

constexpr int kShift = 'a' - 'A';  // потому что так хочу

namespace {

bool IsMatchingPair(char x, char y) {
  return std::abs(x - y) == kShift;
}

std::optional<std::vector<size_t>> FindMapping(const std::string& data) {
  std::vector<size_t> output(data.size() / 2);
  std::list<std::pair<size_t, char>> lst;
  size_t cnt = 0;

  for (size_t i = 0; i < data.size(); ++i) {
    char c = data[i];
    if (std::isupper(c)) {
      lst.emplace_back(i - cnt, c);
    } else {
      lst.emplace_back(++cnt, c);
    }
  }

  while (!lst.empty()) {
    std::pair<size_t, char> fr = lst.front();
    std::pair<size_t, char> bk = lst.back();
    auto sec = std::next(lst.begin());

    if (IsMatchingPair(fr.second, bk.second)) {
      const std::pair<size_t, char> trap = std::isupper(fr.second) ? fr : bk;
      const std::pair<size_t, char> animal = std::isupper(fr.second) ? bk : fr;
      output[trap.first] = animal.first;

      lst.pop_front();
      lst.pop_back();
    } else if (sec != lst.end() && IsMatchingPair(fr.second, sec->second)) {
      const std::pair<size_t, char> trap = std::isupper(fr.second) ? fr : *sec;
      const std::pair<size_t, char> animal = std::isupper(fr.second) ? *sec : fr;
      output[trap.first] = animal.first;

      lst.pop_front();
      lst.pop_front();
    } else {
      break;
    }
  }

  if (!lst.empty()) {
    return std::nullopt;
  }
  return output;
}

}  // namespace

int main() {
  std::string input;
  std::cin >> input;

  std::optional<std::vector<size_t>> result = FindMapping(input);
  if (result) {
    std::cout << "Possible\n";
    for (size_t x : *result) {
      std::cout << x << " ";
    }
  } else {
    std::cout << "Impossible";
  }
  std::cout << "\n";
  return 0;
}
