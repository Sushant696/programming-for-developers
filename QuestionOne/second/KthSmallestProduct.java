package QuestionOne.second;

public class KthSmallestProduct {

    // Returns the kth smallest product among all pairs.
    static int kthSmallestProduct(int[] nums1, int[] nums2, int k) {

        // Defining the range of possible products
        long low = Long.MIN_VALUE;
        long high = Long.MAX_VALUE;

        // Binary search for the smallest product x such that at least k pairs have product <= x
        while (low < high) {
            long mid = low + (high - low) / 2;
            if (countPairs(nums1, nums2, mid) < k) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }
        return (int) low;
    }

    // Count pairs with product <= target
    static long countPairs(int[] nums1, int[] nums2, long target) {
        long count = 0;

        for (int num1 : nums1) {
            if (num1 == 0) {
                // If num1 is 0, all products are 0
                if (target >= 0) {
                    count += nums2.length;
                }
            } else if (num1 > 0) {
                // For positive num1, find how many elements in nums2 give product <= target
                int left = 0, right = nums2.length - 1;
                while (left <= right) {
                    int mid = left + (right - left) / 2;
                    if ((long) num1 * nums2[mid] <= target) {
                        left = mid + 1;
                    } else {
                        right = mid - 1;
                    }
                }
                count += left;
            } else {
                // For negative num1, larger nums2 values give smaller products
                int left = 0, right = nums2.length - 1;
                while (left <= right) {
                    int mid = left + (right - left) / 2;
                    if ((long) num1 * nums2[mid] <= target) {
                        right = mid - 1;  // Look for smaller nums2 values
                    } else {
                        left = mid + 1;   // Look for larger nums2 values
                    }
                }
                count += (nums2.length - left);
            }
        }

        return count;
    }

    public static void main(String[] args) {
        int[] nums1a = {2, 5};
        int[] nums2a = {3, 4};
        int k1 = 2;
        System.out.println("Example 1 Output: " + kthSmallestProduct(nums1a, nums2a, k1)); // Expected: 8

        int[] nums1b = {-4, -2, 0, 3};
        int[] nums2b = {2, 4};
        int k2 = 6;
        System.out.println("Example 2 Output: " + kthSmallestProduct(nums1b, nums2b, k2)); // Expected: 0

        int[] nums1c = {-2, -1, 0, 1, 2};
        int[] nums2c = {-3, -1, 2, 4, 5};
        int k3 = 3;
        System.out.println("Example 3 Output: " + kthSmallestProduct(nums1c, nums2c, k3)); // Expected: -6
    }
}
