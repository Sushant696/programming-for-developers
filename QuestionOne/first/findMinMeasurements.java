package QuestionOne.first;

class findMinMeasurements {

    // Function to find the minimum number of measurements
    public static int findMinMeasurements(int k, int n) {
        // DP array to store the maximum number of temperature levels that can be checked
        int[] dp = new int[k + 1];
        int m = 0; // Number of measurements

        // Loop until the maximum temperature levels checked (dp[k]) >= n
        while (dp[k] < n) {
            m++; // Increment the number of measurements
            // Iterate over the number of samples in reverse order
            for (int i = k; i >= 1; i--) {
                // Update dp[i] using the recurrence relation:
                // covers both case if the  material reacts or do not react
                dp[i] = dp[i - 1] + dp[i] + 1;

            }
        }

        return m; // Return the minimum number of measurements
    }

    public static void main(String[] args) {
// Example 1
        int k1 = 1, n1 = 2;
        System.out.println("Example 1: k = " + k1 + ", n = " + n1);
        System.out.println("Minimum measurements: " + findMinMeasurements(k1, n1)); // Output: 2

// Example 2
        int k2 = 2, n2 = 6;
        System.out.println("Example 2: k = " + k2 + ", n = " + n2);
        System.out.println("Minimum measurements: " + findMinMeasurements(k2, n2)); // Output: 3

// Example 3
        int k3 = 3, n3 = 14;
        System.out.println("Example 3: k = " + k3 + ", n = " + n3);
        System.out.println("Minimum measurements: " + findMinMeasurements(k3, n3)); // Output: 4
    }
}
