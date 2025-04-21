#include <deque>
#include <iostream>
#include <vector>

using namespace std;

vector<long long> slidingWindowMin(const vector<long long>& nums, int K) {
  deque<int> min_candidates;
  vector<long long> result;

  for (int i = 0; i < static_cast<int>(nums.size()); ++i) {
    if (!min_candidates.empty() && min_candidates.front() <= i - K)
      min_candidates.pop_front();

    while (!min_candidates.empty() && nums[min_candidates.back()] >= nums[i])
      min_candidates.pop_back();

    min_candidates.push_back(i);

    if (i >= K - 1)
      result.push_back(nums[min_candidates.front()]);
  }

  return result;
}

int main() {
  ios::sync_with_stdio(false);
  cin.tie(nullptr);

  int N, K;
  if (!(cin >> N >> K) || K == 0 || K > N)
    return 0;

  vector<long long> nums(N);
  for (auto& x : nums)
    cin >> x;

  vector<long long> mins = slidingWindowMin(nums, K);
  for (size_t i = 0; i < mins.size(); ++i) {
    cout << mins[i];
    if (i != mins.size() - 1)
      cout << " ";
  }

  return 0;
}
