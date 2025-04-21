#include <bits/stdc++.h>
using namespace std;

class GoblinQueue {
  deque<int> leftDq, rightDq;

  void balance() {
    if (leftDq.size() > rightDq.size() + 1) {
      rightDq.push_front(leftDq.back());
      leftDq.pop_back();
    } else if (rightDq.size() > leftDq.size()) {
      leftDq.push_back(rightDq.front());
      rightDq.pop_front();
    }
  }

public:
  void push_loh(int id) {
    rightDq.push_back(id);
    balance();
  }

  void push_vip(int id) {
    leftDq.push_back(id);
    balance();
  }

  int pop_front() {
    int res = leftDq.front();
    leftDq.pop_front();
    balance();
    return res;
  }
};

int main() {
  ios::sync_with_stdio(false);
  cin.tie(nullptr);

  int Q;
  cin >> Q;

  GoblinQueue queue;
  while (Q--) {
    char op;
    cin >> op;
    if (op == '+') {
      int id;
      cin >> id;
      queue.push_loh(id);
    } else if (op == '*') {
      int id;
      cin >> id;
      queue.push_vip(id);
    } else {  // '-'
      cout << queue.pop_front() << '\n';
    }
  }
  return 0;
}
