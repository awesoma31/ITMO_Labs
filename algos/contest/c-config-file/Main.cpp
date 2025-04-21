#include <iostream>
#include <list>
#include <string>
#include <unordered_map>

namespace {
auto Split(const std::string& input, char delimiter) -> std::pair<std::string, std::string> {
  const auto pos = input.find(delimiter);
  if (pos != std::string::npos) {
    const auto part1 = input.substr(0, pos);
    const auto part2 = input.substr(pos + 1);
    return std::make_pair(part1, part2);
  }
  return std::make_pair(input, "");
}

struct Change {
  std::string name;
  std::string oldValue;
  bool existed;
};
}  // namespace

auto main() -> int {
  std::unordered_map<std::string, std::string> current_scope;
  std::list<int> block_changes;
  std::list<Change> changes;
  block_changes.push_front(0);

  for (std::string input; std::getline(std::cin, input);) {
    if (input == "{") {
      block_changes.push_front(0);
    } else if (input == "}") {
      int num = block_changes.front();
      block_changes.pop_front();
      while ((num--) != 0) {
        auto& change = changes.back();
        if (!change.existed) {
          current_scope.erase(change.name);
        } else {
          current_scope[change.name] = change.oldValue;
        }
        changes.pop_back();
      }
    } else {
      auto [name, value] = Split(input, '=');
      bool existed = (current_scope.find(name) != current_scope.end());
      std::string old_val = existed ? current_scope[name] : "";
      block_changes.front()++;

      if (!value.empty() && (std::isdigit(value.front()) == 0) && value.front() != '-') {
        auto scope_value = current_scope.find(value);
        if (scope_value == current_scope.end()) {
          value = "0";
        } else {
          value = scope_value->second;
        }
        std::cout << value << "\n";
      }

      changes.emplace_back(name, old_val, existed);
      current_scope[name] = value;
    }
  }
}
