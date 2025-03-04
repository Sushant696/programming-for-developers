package QuestionThree.first;

import java.util.Comparator;
import java.util.PriorityQueue;

class MinConnectionCost {

    public int minimumCost(int n, int[] moduleCosts, int[][] connectionCosts) {
        // Min-heap to process connections by cost
        PriorityQueue<Connection> pq = new PriorityQueue<>(Comparator.comparingInt(c -> c.cost));

        // Add module installation costs (virtual connections to cloud)
        for (int i = 1; i <= n; i++) {
            pq.add(new Connection(0, i, moduleCosts[i - 1]));
        }

        // Add direct connections between devices
        for (int[] conn : connectionCosts) {
            pq.add(new Connection(conn[0], conn[1], conn[2]));
        }

        // Union-Find to track connected components
        DisjointSet ds = new DisjointSet(n + 1); // +1 for cloud node

        int totalCost = 0;
        int connectionsNeeded = n; // Need n connections for n+1 nodes

        // Kruskal's algorithm: process connections in order of cost
        while (!pq.isEmpty() && connectionsNeeded > 0) {
            Connection conn = pq.poll();

            // If devices are not connected, connect them
            if (ds.find(conn.device1) != ds.find(conn.device2)) {
                ds.union(conn.device1, conn.device2);
                totalCost += conn.cost;
                connectionsNeeded--;
            }
        }

        return totalCost;
    }

    // Represents a connection between two devices or a module installation
    static class Connection {
        int device1, device2, cost;

        public Connection(int device1, int device2, int cost) {
            this.device1 = device1;
            this.device2 = device2;
            this.cost = cost;
        }
    }

    // Union-Find with path compression and union by rank
    static class DisjointSet {
        int[] parent, rank;

        public DisjointSet(int size) {
            parent = new int[size];
            rank = new int[size];
            for (int i = 0; i < size; i++) {
                parent[i] = i; // Each node is its own parent initially
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

            if (rootX == rootY)
                return;

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
        int[] moduleCosts = { 1, 2, 2 };
        int[][] connectionCosts = { { 1, 2, 1 }, { 2, 3, 1 } };

        System.out.println("Minimum cost: " + solution.minimumCost(n, moduleCosts, connectionCosts));
    }
}