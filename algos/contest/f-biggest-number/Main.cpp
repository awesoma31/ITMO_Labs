
#include <algorithm>
#include <iostream>
#include <string>
#include <vector>

namespace {

bool Compare(const std::string& a, const std::string& b) {
  return a + b > b + a;
}

void SortPieces(std::vector<std::string>& pieces) {
  std::sort(pieces.begin(), pieces.end(), Compare);
}

std::string FindMaxNumber(std::vector<std::string>& pieces) {
  SortPieces(pieces);

  std::string result;
  for (std::size_t i = 0; i < pieces.size(); ++i) {
    result += pieces[i];
  }

  std::size_t first_non_zero = result.find_first_not_of('0');
  if (first_non_zero == std::string::npos) {
    return "0";
  }

  return result.substr(first_non_zero);
}

}  // namespace

int main() {
  std::vector<std::string> pieces;
  std::string input;

  while (std::cin >> input) {
    pieces.push_back(input);
  }

  std::string max_number = FindMaxNumber(pieces);
  std::cout << max_number << std::endl;

  return 0;
}
