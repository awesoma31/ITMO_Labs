#include <iostream>
#include <vector>

using namespace std;

int main() {
  ios_base::sync_with_stdio(false);
  cin.tie(nullptr);

  int n;
  cin >> n;
  vector<long long> arr(n);
  for (int i = 0; i < n; i++) {
    cin >> arr[i];
  }

  int ansStart = 0, ansEnd = 0;
  int maxLength = 0;
  int curStart = 0;

  for (int i = 2; i < n; i++) {
    if (arr[i] == arr[i - 1] && arr[i - 1] == arr[i - 2]) {
      int curLength = i - curStart;
      if (curLength > maxLength) {
        maxLength = curLength;
        ansStart = curStart;
        ansEnd = i - 1;
      }
      curStart = i - 1;
    }
  }

  int curLength = n - curStart;
  if (curLength > maxLength) {
    ansStart = curStart;
    ansEnd = n - 1;
  }

  cout << ansStart + 1 << " " << ansEnd + 1;
  return 0;
}
