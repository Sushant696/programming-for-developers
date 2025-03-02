package QuestionOne.second;

public class KthSmallestProduct {

    // Returns the kth smallest product among all pairs.
    static int kthSmallestProduct(int[] nums1, int[] nums2, int k) {
        long low = Long.MIN_VALUE, high = Long.MAX_VALUE;

        // Binary search for the smallest product x such that at least k pairs have
        // product <= x
        while (low < high) {
            long mid = low + (high - low) / 2;
            long count = countPairs(nums1, nums2, mid);
            if (count < k) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }
        return (int) low;
    }

    static long countPairs(int[] nums1, int[] nums2, long x) {
        long count = 0;
        for (int a : nums1) {
            if (a > 0) {
                // For positive a, find the largest b such that a * b <= x
                int left = 0, right = nums2.length;
                while (left < right) {
                    int mid = (left + right) / 2;
                    if ((long) a * nums2[mid] <= x) {
                        left = mid + 1;
                    } else {
                        right = mid;
                    }
                }
                count += left; // All b's from 0 to left-1 are valid
            } else if (a == 0) {
                // If a == 0, all b's are valid if x >= 0
                if (x >= 0) {
                    count += nums2.length;
                }
            } else {
                // For negative a, find the smallest b.
                int left = 0, right = nums2.length;
                while (left < right) {
                    int mid = (left + right) / 2;
                    if ((long) a * nums2[mid] <= x) {
                        right = mid;
                    } else {
                        left = mid + 1;
                    }
                }
                count += (nums2.length - left);
            }
        }
        return count;
    }

    public static void main(String[] args) {
        int[] nums1a = { 2, 5 };
        int[] nums2a = { 3, 4 };
        int k1 = 2;
        System.out.println("Example 1 Output: " + kthSmallestProduct(nums1a, nums2a, k1)); // Expected: 8

        int[] nums1b = { -4, -2, 0, 3 };
        int[] nums2b = { 2, 4 };
        int k2 = 6;
        System.out.println("Example 2 Output: " + kthSmallestProduct(nums1b, nums2b, k2)); // Expected: 0

        int[] nums1c = { -2, -1, 0, 1, 2 };
        int[] nums2c = { -3, -1, 2, 4, 5 };
        int k3 = 3;
        System.out.println("Example 3 Output: " + kthSmallestProduct(nums1c, nums2c, k3)); // Expected: -6
    }
}