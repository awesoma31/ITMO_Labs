
#include <algorithm>
#include <iostream>
#include <vector>

using namespace std;

bool CanPlace(const vector<long long>& positions, int cows, long long dist) {
  int count = 1;
  long long lastPlaced = positions[0];

  for (size_t i = 1; i < positions.size(); ++i) {
    if (positions[i] - lastPlaced >= dist) {
      ++count;
      lastPlaced = positions[i];
      if (count == cows)
        return true;
    }
  }

  return count == cows;
}

long long FindBestDistance(const vector<long long>& positions, int cows) {
  long long low = 1;
  long long high = positions.back() - positions[0];
  long long best = 0;

  while (low <= high) {
    long long mid = (low + high) / 2;
    if (CanPlace(positions, cows, mid)) {
      best = mid;
      low = mid + 1;
    } else {
      high = mid - 1;
    }
  }

  return best;
}  // namespace

int main() {
  int n, k;
  cin >> n >> k;

  vector<long long> stalls(n);
  for (int i = 0; i < n; ++i) {
    cin >> stalls[i];
  }

  sort(stalls.begin(), stalls.end());

  cout << FindBestDistance(stalls, k) << endl;
  return 0;
}
