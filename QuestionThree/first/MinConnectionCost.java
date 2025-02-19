package QuestionThree.first;

import java.util.*;

class MinConnectionCost {

    public int minimumCost(int n, int[] moduleCosts, int[][] connectionCosts) {
        // Create a list for all possible connections
        List<Connection> allConnections = new ArrayList<>();

        // Add "virtual connections" representing module installations
        // Installing a module on device i connects it to the "cloud" (node 0)
        for (int i = 1; i <= n; i++) {
            allConnections.add(new Connection(0, i, moduleCosts[i - 1]));
        }

        // Add all direct connections between devices
        for (int[] conn : connectionCosts) {
            allConnections.add(new Connection(conn[0], conn[1], conn[2]));
        }

        // Sort all connections by cost (cheapest first)
        Collections.sort(allConnections, Comparator.comparingInt(c -> c.cost));

        // Initialize Union-Find data structure to track connected components
        DisjointSet ds = new DisjointSet(n + 1); // +1 for the virtual "cloud" node

        int totalCost = 0;
        int connectionsNeeded = n; // We need n connections for n+1 nodes (including virtual cloud)

        // Kruskal's algorithm - add connections in order of increasing cost
        for (Connection conn : allConnections) {
            // If these nodes aren't already connected
            if (ds.find(conn.device1) != ds.find(conn.device2)) {
                // Connect them
                ds.union(conn.device1, conn.device2);
                totalCost += conn.cost;
                connectionsNeeded--;

                // If all devices are connected, we're done
                if (connectionsNeeded == 0) {
                    break;
                }
            }
        }

        return totalCost;
    }

    // Represents either a direct connection between devices or a module installation
    static class Connection {

        int device1;
        int device2;
        int cost;

        public Connection(int device1, int device2, int cost) {
            this.device1 = device1;
            this.device2 = device2;
            this.cost = cost;
        }
    }

    // Union-Find data structure to efficiently track connected components
    static class DisjointSet {

        int[] parent;
        int[] rank;

        public DisjointSet(int size) {
            parent = new int[size];
            rank = new int[size];

            // Initially, each node is its own parent
            for (int i = 0; i < size; i++) {
                parent[i] = i;
            }
        }

        // Find with path compression
        public int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        // Union by rank
        public void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);

            if (rootX == rootY) {
                return;
            }

            if (rank[rootX] < rank[rootY]) {
                parent[rootX] = rootY;
            } else if (rank[rootX] > rank[rootY]) {
                parent[rootY] = rootX;
            } else {
                parent[rootY] = rootX;
                rank[rootX]++;
            }
        }
    }

    // Example usage
    public static void main(String[] args) {
        MinConnectionCost solution = new MinConnectionCost();
        int n = 3;
        int[] moduleCosts = {1, 2, 2};
        int[][] connectionCosts = {{1, 2, 1}, {2, 3, 1}};

        System.out.println("Minimum cost: " + solution.minimumCost(n, moduleCosts, connectionCosts));
    }
}
