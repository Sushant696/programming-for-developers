package QuestionFour.second;

import java.util.*;

public class MinRoadTravel {

    public static int minRoadsToCollectPackages(int[] packages, int[][] roads) {
        int n = packages.length;

        // Build adjacency list
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }
        for (int[] road : roads) {
            int a = road[0];
            int b = road[1];
            graph.get(a).add(b);
            graph.get(b).add(a);
        }

        // Find all package locations
        List<Integer> packageLocations = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (packages[i] == 1) {
                packageLocations.add(i);
            }
        }

        if (packageLocations.isEmpty()) {
            return 0; // No packages to collect
        }

        // Precompute shortest distances from each node to all other nodes using BFS
        int[][] dist = new int[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(dist[i], Integer.MAX_VALUE);
            dist[i][i] = 0;
            Queue<Integer> queue = new LinkedList<>();
            queue.add(i);
            while (!queue.isEmpty()) {
                int u = queue.poll();
                for (int v : graph.get(u)) {
                    if (dist[i][v] == Integer.MAX_VALUE) {
                        dist[i][v] = dist[i][u] + 1;
                        queue.add(v);
                    }
                }
            }
        }

        // Use BFS to find the minimum roads to collect all packages
        int minRoads = Integer.MAX_VALUE;

        for (int start = 0; start < n; start++) {
            Queue<State> queue = new LinkedList<>();
            Set<String> visited = new HashSet<>();

            // Initial state: start location, no packages collected, 0 roads traversed
            queue.add(new State(start, 0, 0));

            while (!queue.isEmpty()) {
                State current = queue.poll();
                int loc = current.location;
                int collectedMask = current.collectedMask;
                int roadsTraversed = current.roadsTraversed;

                // If all packages collected, update minRoads
                if (collectedMask == (1 << packageLocations.size()) - 1) {
                    minRoads = Math.min(minRoads, roadsTraversed);
                    continue;
                }

                // Generate unique state key
                String stateKey = loc + ":" + collectedMask;
                if (visited.contains(stateKey)) {
                    continue;
                }
                visited.add(stateKey);

                // Try moving to adjacent locations
                for (int nextLoc : graph.get(loc)) {
                    int newCollectedMask = collectedMask;
                    for (int i = 0; i < packageLocations.size(); i++) {
                        if (packageLocations.get(i) == nextLoc) {
                            newCollectedMask |= (1 << i); // Mark package as collected
                        }
                    }
                    queue.add(new State(nextLoc, newCollectedMask, roadsTraversed + 1));
                }
            }
        }

        return minRoads != Integer.MAX_VALUE ? minRoads : -1;
    }

    // Helper class to represent state in BFS
    static class State {
        int location;
        int collectedMask; // Bitmask to represent collected packages
        int roadsTraversed; // Number of roads traversed so far

        State(int location, int collectedMask, int roadsTraversed) {
            this.location = location;
            this.collectedMask = collectedMask;
            this.roadsTraversed = roadsTraversed;
        }
    }

    public static void main(String[] args) {
        // Test case 1
        int[] packages1 = { 1, 0, 0, 0, 0, 1 };
        int[][] roads1 = { { 0, 1 }, { 1, 2 }, { 2, 3 }, { 3, 4 }, { 4, 5 } };
        System.out.println(minRoadsToCollectPackages(packages1, roads1)); // Should output 2

        // Test case 2
        int[] packages2 = { 0, 0, 0, 1, 1, 0, 0, 1 };
        int[][] roads2 = { { 0, 1 }, { 0, 2 }, { 1, 3 }, { 1, 4 }, { 2, 5 }, { 5, 6 }, { 5, 7 } };
        System.out.println(minRoadsToCollectPackages(packages2, roads2)); // Should output 2
    }
}