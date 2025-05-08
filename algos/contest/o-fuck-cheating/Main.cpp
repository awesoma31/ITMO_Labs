#include <bits/stdc++.h>
using namespace std;

int main() {
  ios::sync_with_stdio(false);
  cin.tie(nullptr);

  int n, m;
  cin >> n >> m;
  vector<vector<int>> g(n + 1);
  while (m--) {
    int u, v;
    cin >> u >> v;
    g[u].push_back(v);
    g[v].push_back(u);
  }

  vector<int> color(n + 1, -1);
  queue<int> q;

  for (int i = 1; i <= n; ++i) {
    if (color[i] != -1)
      continue;
    color[i] = 0;
    q.push(i);
    while (!q.empty()) {
      int x = q.front();
      q.pop();
      for (int y : g[x]) {
        if (color[y] == -1) {
          color[y] = color[x] ^ 1;
          q.push(y);
        } else if (color[y] == color[x]) {
          cout << "NO\n";
          return 0;
        }
      }
    }
  }
  cout << "YES\n";
  return 0;
}
