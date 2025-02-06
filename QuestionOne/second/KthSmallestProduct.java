package QuestionOne.second;

public class KthSmallestProduct {

    // Returns the kth smallest product among all pairs (nums1[i] * nums2[j])
    static int kthSmallestProduct(int[] nums1, int[] nums2, int k) {
        int n = nums1.length, m = nums2.length;

        // Determine search boundaries by considering the four corner products.
        long low = Long.MAX_VALUE;
        long high = Long.MIN_VALUE;
        long[] corners = new long[]{
            (long) nums1[0] * nums2[0],
            (long) nums1[0] * nums2[m - 1],
            (long) nums1[n - 1] * nums2[0],
            (long) nums1[n - 1] * nums2[m - 1]
        };
        for (long prod : corners) {
            low = Math.min(low, prod);
            high = Math.max(high, prod);
        }

        // Binary search for the smallest product X such that at least k pairs have product <= X.
        while (low < high) {
            long mid = low + (high - low) / 2;
            long count = countLE(mid, nums1, nums2);
            if (count < k) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }
        return (int) low;
    }

    // countLE(x, nums1, nums2) counts how many pairs (i, j) satisfy nums1[i] * nums2[j] <= x.
    static long countLE(long x, int[] nums1, int[] nums2) {
        long count = 0;
        int m = nums2.length;

        for (int a : nums1) {
            if (a > 0) {
                // For positive a, we want b such that: a * b <= x  ==>  b <= x / a.
                int l = 0, r = m;
                while (l < r) {
                    int mid = (l + r) / 2;
                    if ((long) a * nums2[mid] <= x) {
                        l = mid + 1;
                    } else {
                        r = mid;
                    }
                }
                count += l; // l is the number of valid b's.
            } else if (a == 0) {
                // If a == 0, then a*b == 0. If x >= 0, all m products (0*b) are <= x.
                if (x >= 0) {
                    count += m;
                }
            } else { // a < 0
                // For negative a, note that as b increases, a*b becomes smaller.
                // We want b such that: a * b <= x. Because a is negative, divide by a (and reverse inequality):
                // This becomes: b >= ceil(x / a). We can count how many b's in nums2 (which is sorted in ascending order)
                // satisfy this by doing a binary search.
                int l = 0, r = m;
                while (l < r) {
                    int mid = (l + r) / 2;
                    if ((long) a * nums2[mid] <= x) {
                        r = mid;
                    } else {
                        l = mid + 1;
                    }
                }
                count += (m - l); // All indices from l to m-1 satisfy the inequality.
            }
        }
        return count;
    }

    public static void main(String[] args) {
        // Example 1:
        int[] nums1a = {2, 5};
        int[] nums2a = {3, 4};
        int k1 = 2;
        // Products: 2*3=6, 2*4=8, 5*3=15, 5*4=20. The 2nd smallest is 8.
        System.out.println("Example 1 Output: " + kthSmallestProduct(nums1a, nums2a, k1));  // Expected: 8

        // Example 2:
        int[] nums1b = {-4, -2, 0, 3};
        int[] nums2b = {2, 4};
        int k2 = 6;
        // The 6 smallest products (in sorted order): -16, -8, -8, -4, 0, 0, 6, 12. The 6th smallest is 0.
        System.out.println("Example 2 Output: " + kthSmallestProduct(nums1b, nums2b, k2));  // Expected: 0

        // Example 3:
        int[] nums1c = {-2, -1, 0, 1, 2};
        int[] nums2c = {-3, -1, 2, 4, 5};
        int k3 = 3;
        // One possible sorted order: -10, -8, -6, ... The 3rd smallest is -6.
        System.out.println("Example 3 Output: " + kthSmallestProduct(nums1c, nums2c, k3));  // Expected: -6
    }
}
