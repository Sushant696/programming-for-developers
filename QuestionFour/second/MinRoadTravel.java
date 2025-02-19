package QuestionFour.second;

import java.util.*;

public class MinRoadTravel {

    public static int minRoadsToCollectPackages(int[] packages, int[][] roads) {
        int n = packages.length;

        // Build adjacency list representation of the graph
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

        // Find locations with packages
        List<Integer> packageLocations = new ArrayList<>();
        for (int i = 0; i < packages.length; i++) {
            if (packages[i] == 1) {
                packageLocations.add(i);
            }
        }

        if (packageLocations.isEmpty()) {
            return 0; // No packages to collect
        }

        // Compute distances between all nodes (Floyd-Warshall)
        int[][] dist = new int[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(dist[i], Integer.MAX_VALUE / 2); // Use MAX_VALUE/2 to prevent overflow
            dist[i][i] = 0;
        }

        for (int u = 0; u < n; u++) {
            for (int v : graph.get(u)) {
                dist[u][v] = 1;
            }
        }

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }

        // For each location, determine which package locations are within distance 2
        List<Set<Integer>> canCollect = new ArrayList<>();
        for (int loc = 0; loc < n; loc++) {
            Set<Integer> collectable = new HashSet<>();
            for (int pkgLoc : packageLocations) {
                if (dist[loc][pkgLoc] <= 2) {
                    collectable.add(pkgLoc);
                }
            }
            canCollect.add(collectable);
        }

        // Use BFS to find minimum roads traversed
        int minRoads = Integer.MAX_VALUE;

        for (int start = 0; start < n; start++) {
            Queue<State> queue = new LinkedList<>();
            Set<String> visited = new HashSet<>();

            // Initial state: current location, collected packages, roads traversed
            Set<Integer> initialCollected = new HashSet<>(canCollect.get(start));
            queue.add(new State(start, initialCollected, 0));

            while (!queue.isEmpty()) {
                State current = queue.poll();
                int loc = current.location;
                Set<Integer> collected = current.collected;
                int currentRoads = current.roads;

                // If all packages collected, find path back to start
                if (collected.size() == packageLocations.size()) {
                    int returnDistance = dist[loc][start];
                    if (returnDistance < Integer.MAX_VALUE / 2) {
                        minRoads = Math.min(minRoads, currentRoads + returnDistance);
                    }
                    continue;
                }

                // Generate unique state key
                String stateKey = loc + ":" + collected.hashCode();
                if (visited.contains(stateKey)) {
                    continue;
                }
                visited.add(stateKey);

                // Try moving to adjacent locations
                for (int nextLoc : graph.get(loc)) {
                    Set<Integer> newCollected = new HashSet<>(collected);
                    newCollected.addAll(canCollect.get(nextLoc));
                    queue.add(new State(nextLoc, newCollected, currentRoads + 1));
                }
            }
        }

        return minRoads != Integer.MAX_VALUE ? minRoads : -1;
    }

    // Helper class to represent state in BFS
    static class State {

        int location;
        Set<Integer> collected;
        int roads;

        State(int location, Set<Integer> collected, int roads) {
            this.location = location;
            this.collected = collected;
            this.roads = roads;
        }
    }

    public static void main(String[] args) {
        // Test case 1
        int[] packages1 = {1, 0, 0, 0, 0, 1};
        int[][] roads1 = {{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 5}};
        System.out.println(minRoadsToCollectPackages(packages1, roads1)); // Should output 2

        // Test case 2
        int[] packages2 = {0, 0, 0, 1, 1, 0, 0, 1};
        int[][] roads2 = {{0, 1}, {0, 2}, {1, 3}, {1, 4}, {2, 5}, {5, 6}, {5, 7}};
        System.out.println(minRoadsToCollectPackages(packages2, roads2)); // Should output 2
    }
}
