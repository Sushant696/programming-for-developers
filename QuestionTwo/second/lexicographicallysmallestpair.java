package QuestionTwo.second;

public class lexicographicallysmallestpair {

    // Method to find the lexicographically smallest pair with the smallest Manhattan distance
    public static int[] findClosestPair(int[] x_coords, int[] y_coords) {
        int n = x_coords.length;
        int minDistance = Integer.MAX_VALUE;
        int[] closestPair = new int[2]; // To store the indices of the closest pair

        // Iterate through all pairs of points
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                // Calculate Manhattan distance
                int distance = Math.abs(x_coords[i] - x_coords[j]) + Math.abs(y_coords[i] - y_coords[j]);

                // Update the closest pair if a smaller distance is found
                if (distance < minDistance
                        || (distance == minDistance && isLexicographicallySmaller(i, j, closestPair[0], closestPair[1]))) {
                    minDistance = distance;
                    closestPair[0] = i;
                    closestPair[1] = j;
                }
            }
        }

        return closestPair;
    }

    // Helper method to check if one pair is lexicographically smaller than another
    private static boolean isLexicographicallySmaller(int i1, int j1, int i2, int j2) {
        if (i1 < i2) {
            return true;
        } else if (i1 == i2 && j1 < j2) {
            return true;
        }
        return false;
    }

    // Main method for testing
    public static void main(String[] args) {
        // Example Input
        int[] x_coords = {1, 2, 3, 2, 4};
        int[] y_coords = {2, 3, 1, 2, 3};

        // Find the closest pair
        int[] result = findClosestPair(x_coords, y_coords);

        // Print the result
        System.out.println("Closest Pair Indices: [" + result[0] + ", " + result[1] + "]");
    }
}
