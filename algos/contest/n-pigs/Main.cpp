#include <bits/stdc++.h>
using namespace std;

struct DisjointSet {
  vector<int> p, r;
  DisjointSet(int n) : p(n + 1), r(n + 1) {
    iota(p.begin(), p.end(), 0);
  }

  int leader(int v) {
    return p[v] == v ? v : p[v] = leader(p[v]);
  }

  bool merge(int a, int b) {
    a = leader(a);
    b = leader(b);
    if (a == b)
      return false;
    if (r[a] < r[b])
      swap(a, b);
    p[b] = a;
    if (r[a] == r[b])
      ++r[a];
    return true;
  }
};

int main() {
  ios::sync_with_stdio(false);
  cin.tie(nullptr);

  int banks_amount;
  if (!(cin >> banks_amount))
    return 0;
  vector<int> keyIn(banks_amount + 1);
  for (int i = 1; i <= banks_amount; ++i)
    cin >> keyIn[i];

  DisjointSet dsu(banks_amount);
  int broken = 0;

  for (int i = 1; i <= banks_amount; ++i) {
    if (!dsu.merge(i, keyIn[i]))
      ++broken;
  }

  cout << broken << "\n";
  return 0;
}
