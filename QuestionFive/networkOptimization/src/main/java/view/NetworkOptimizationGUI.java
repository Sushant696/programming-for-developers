package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

public class NetworkOptimizationGUI extends JFrame {
    // Main panels
    private JPanel controlPanel;
    private DrawingPanel drawingPanel;
    private JPanel statusPanel;

    // Control elements
    private JButton addServerButton;
    private JButton addClientButton;
    private JButton addConnectionButton;
    private JButton findOptimalNetworkButton;
    private JButton calculatePathButton;
    private JButton clearButton;
    private JLabel modeLabel;

    // Status displays
    private JLabel totalCostLabel;
    private JLabel avgLatencyLabel;

    // Node selection for path calculation
    private JComboBox<String> sourceNodeComboBox;
    private JComboBox<String> destinationNodeComboBox;

    // Application state
    private enum Mode {
        ADD_SERVER, ADD_CLIENT, ADD_CONNECTION, NORMAL
    }

    private Mode currentMode = Mode.NORMAL;
    private Node selectedNode = null;
    private List<Node> nodes = new ArrayList<>();
    private List<Connection> connections = new ArrayList<>();
    private List<Connection> optimalConnections = new ArrayList<>();
    private List<Connection> shortestPath = new ArrayList<>();

    public NetworkOptimizationGUI() {
        setTitle("Network Optimization Tool");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize panels
        initControlPanel();
        initDrawingPanel();
        initStatusPanel();

        // Add panels to the frame
        add(controlPanel, BorderLayout.NORTH);
        add(drawingPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);

        updateStatusBar();
        updateNodeComboBoxes();
    }

    private void initControlPanel() {
        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        addServerButton = new JButton("Add Server");
        addClientButton = new JButton("Add Client");
        addConnectionButton = new JButton("Add Connection");
        findOptimalNetworkButton = new JButton("Find Optimal Network");
        calculatePathButton = new JButton("Calculate Path");
        clearButton = new JButton("Clear All");

        // Add action listeners
        addServerButton.addActionListener(e -> {
            currentMode = Mode.ADD_SERVER;
            updateStatusBar();
        });

        addClientButton.addActionListener(e -> {
            currentMode = Mode.ADD_CLIENT;
            updateStatusBar();
        });

        addConnectionButton.addActionListener(e -> {
            currentMode = Mode.ADD_CONNECTION;
            selectedNode = null;
            updateStatusBar();
        });

        findOptimalNetworkButton.addActionListener(e -> {
            findOptimalNetwork();
            drawingPanel.repaint();
            updateStatusBar();
        });

        calculatePathButton.addActionListener(e -> {
            calculateShortestPath();
            drawingPanel.repaint();
            updateStatusBar();
        });

        clearButton.addActionListener(e -> {
            nodes.clear();
            connections.clear();
            optimalConnections.clear();
            shortestPath.clear();
            currentMode = Mode.NORMAL;
            selectedNode = null;
            updateNodeComboBoxes();
            drawingPanel.repaint();
            updateStatusBar();
        });

        // Add components to panel
        controlPanel.add(addServerButton);
        controlPanel.add(addClientButton);
        controlPanel.add(addConnectionButton);
        controlPanel.add(findOptimalNetworkButton);
        controlPanel.add(calculatePathButton);
        controlPanel.add(clearButton);
    }

    private void initDrawingPanel() {
        drawingPanel = new DrawingPanel();
    }

    private void initStatusPanel() {
        statusPanel = new JPanel();
        statusPanel.setLayout(new BorderLayout());

        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        totalCostLabel = new JLabel("Total Cost: 0");
        avgLatencyLabel = new JLabel("Average Latency: 0");

        modeLabel = new JLabel("Current Mode: Normal");
        statsPanel.add(modeLabel);

        statsPanel.add(totalCostLabel);
        statsPanel.add(avgLatencyLabel);

        JPanel pathPanel = new JPanel();
        pathPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        sourceNodeComboBox = new JComboBox<>();
        destinationNodeComboBox = new JComboBox<>();

        pathPanel.add(new JLabel("Source:"));
        pathPanel.add(sourceNodeComboBox);
        pathPanel.add(new JLabel("Destination:"));
        pathPanel.add(destinationNodeComboBox);

        statusPanel.add(statsPanel, BorderLayout.WEST);
        statusPanel.add(pathPanel, BorderLayout.EAST);
    }

    private void updateStatusBar() {
        double totalCost = calculateTotalCost();
        double avgLatency = calculateAverageLatency();

        totalCostLabel.setText("Total Cost: " + String.format("%.2f", totalCost));
        avgLatencyLabel.setText("Average Latency: " + String.format("%.2f", avgLatency));

        String modeText = "Current Mode: ";
        switch (currentMode) {
            case ADD_SERVER:
                modeText += "Adding Server";
                break;
            case ADD_CLIENT:
                modeText += "Adding Client";
                break;
            case ADD_CONNECTION:
                modeText += "Adding Connection";
                if (selectedNode != null) {
                    modeText += " (Select second node)";
                }
                break;
            case NORMAL:
                modeText += "Normal";
                break;
        }
        modeLabel.setText(modeText);
    }

    private double calculateTotalCost() {
        double cost = 0;
        for (Connection conn : optimalConnections.isEmpty() ? connections : optimalConnections) {
            cost += conn.cost;
        }
        return cost;
    }

    private double calculateAverageLatency() {
        if ((optimalConnections.isEmpty() ? connections : optimalConnections).isEmpty()) {
            return 0;
        }

        double totalLatency = 0;
        for (Connection conn : optimalConnections.isEmpty() ? connections : optimalConnections) {
            // Calculate latency as inverse of bandwidth
            totalLatency += (1.0 / conn.bandwidth);
        }

        return totalLatency / (optimalConnections.isEmpty() ? connections : optimalConnections).size();
    }

    private void updateNodeComboBoxes() {
        sourceNodeComboBox.removeAllItems();
        destinationNodeComboBox.removeAllItems();

        for (Node node : nodes) {
            sourceNodeComboBox.addItem(node.id);
            destinationNodeComboBox.addItem(node.id);
        }
    }

    private void findOptimalNetwork() {
        // Reset optimal connections
        optimalConnections.clear();

        // Use Kruskal's algorithm to find minimum spanning tree
        List<Connection> allConnections = new ArrayList<>(connections);
        // Sort connections by cost
        Collections.sort(allConnections, Comparator.comparingDouble(c -> c.cost));

        // Union-find data structure
        Map<Node, Node> parent = new HashMap<>();
        for (Node node : nodes) {
            parent.put(node, node);
        }

        for (Connection conn : allConnections) {
            Node rootA = findRoot(parent, conn.node1);
            Node rootB = findRoot(parent, conn.node2);

            if (rootA != rootB) {
                // Add this connection to the optimal network
                optimalConnections.add(conn);
                // Union the sets
                parent.put(rootA, rootB);

                // Check if all nodes are connected
                if (optimalConnections.size() == nodes.size() - 1) {
                    break;
                }
            }
        }
    }

    private Node findRoot(Map<Node, Node> parent, Node node) {
        if (parent.get(node) != node) {
            parent.put(node, findRoot(parent, parent.get(node)));
        }
        return parent.get(node);
    }

    private void calculateShortestPath() {
        // Reset the shortest path
        shortestPath.clear();

        if (sourceNodeComboBox.getSelectedItem() == null ||
                destinationNodeComboBox.getSelectedItem() == null) {
            return;
        }

        String sourceId = (String) sourceNodeComboBox.getSelectedItem();
        String destId = (String) destinationNodeComboBox.getSelectedItem();

        Node sourceNode = null;
        Node destNode = null;

        for (Node node : nodes) {
            if (node.id.equals(sourceId)) {
                sourceNode = node;
            }
            if (node.id.equals(destId)) {
                destNode = node;
            }
        }

        if (sourceNode == null || destNode == null) {
            return;
        }

        // Use Dijkstra's algorithm to find shortest path based on bandwidth (inverse
        // for latency)
        Map<Node, Double> distance = new HashMap<>();
        Map<Node, Node> prev = new HashMap<>();
        Map<Node, Connection> connMap = new HashMap<>();

        for (Node node : nodes) {
            distance.put(node, Double.POSITIVE_INFINITY);
            prev.put(node, null);
        }

        distance.put(sourceNode, 0.0);

        PriorityQueue<Node> queue = new PriorityQueue<>(
                Comparator.comparingDouble(distance::get));
        queue.addAll(nodes);

        // Build adjacency list
        Map<Node, List<Connection>> adjacencyList = new HashMap<>();
        for (Node node : nodes) {
            adjacencyList.put(node, new ArrayList<>());
        }

        for (Connection conn : optimalConnections.isEmpty() ? connections : optimalConnections) {
            adjacencyList.get(conn.node1).add(conn);
            adjacencyList.get(conn.node2).add(conn);
        }

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            if (current == destNode) {
                break;
            }

            if (distance.get(current) == Double.POSITIVE_INFINITY) {
                break;
            }

            for (Connection conn : adjacencyList.get(current)) {
                Node neighbor = (conn.node1 == current) ? conn.node2 : conn.node1;

                // Calculate latency as inverse of bandwidth
                double latency = 1.0 / conn.bandwidth;
                double alt = distance.get(current) + latency;

                if (alt < distance.get(neighbor)) {
                    distance.put(neighbor, alt);
                    prev.put(neighbor, current);
                    connMap.put(neighbor, conn);

                    // Re-sort the queue
                    queue.remove(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        // Reconstruct path
        Node current = destNode;
        while (prev.get(current) != null) {
            Connection conn = connMap.get(current);
            shortestPath.add(0, conn);
            current = prev.get(current);
        }
    }

    private class DrawingPanel extends JPanel {
        private final int NODE_RADIUS = 20;

        public DrawingPanel() {
            setBackground(Color.WHITE);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    switch (currentMode) {
                        case ADD_SERVER:
                            addServer(e.getX(), e.getY());
                            break;
                        case ADD_CLIENT:
                            addClient(e.getX(), e.getY());
                            break;
                        case ADD_CONNECTION:
                            handleConnectionMode(e.getX(), e.getY());
                            break;
                        case NORMAL:
                            selectNode(e.getX(), e.getY());
                            break;
                    }
                    repaint();
                }
            });
        }

        private void addServer(int x, int y) {
            String id = "Server" + (nodes.size() + 1);
            nodes.add(new Node(id, x, y, NodeType.SERVER));
            currentMode = Mode.NORMAL;
            updateNodeComboBoxes();
            updateStatusBar();
        }

        private void addClient(int x, int y) {
            String id = "Client" + (nodes.size() + 1);
            nodes.add(new Node(id, x, y, NodeType.CLIENT));
            currentMode = Mode.NORMAL;
            updateNodeComboBoxes();
            updateStatusBar();
        }

        private void handleConnectionMode(int x, int y) {
            Node clickedNode = getNodeAt(x, y);

            if (clickedNode == null) {
                return;
            }

            if (selectedNode == null) {
                selectedNode = clickedNode;
                updateStatusBar();
            } else if (selectedNode != clickedNode) {
                // Check if connection already exists
                boolean connectionExists = false;
                for (Connection conn : connections) {
                    if ((conn.node1 == selectedNode && conn.node2 == clickedNode) ||
                            (conn.node1 == clickedNode && conn.node2 == selectedNode)) {
                        connectionExists = true;
                        break;
                    }
                }

                if (!connectionExists) {
                    // Prompt for cost and bandwidth
                    String costStr = JOptionPane.showInputDialog(this,
                            "Enter cost for this connection:", "100");
                    String bandwidthStr = JOptionPane.showInputDialog(this,
                            "Enter bandwidth for this connection (Mbps):", "10");

                    try {
                        double cost = Double.parseDouble(costStr);
                        double bandwidth = Double.parseDouble(bandwidthStr);

                        connections.add(new Connection(selectedNode, clickedNode, cost, bandwidth));

                        // Reset optimal and shortest paths
                        optimalConnections.clear();
                        shortestPath.clear();
                    } catch (NumberFormatException | NullPointerException ex) {
                        JOptionPane.showMessageDialog(this,
                                "Invalid input. Using default values.",
                                "Input Error", JOptionPane.ERROR_MESSAGE);

                        connections.add(new Connection(selectedNode, clickedNode, 100, 10));
                    }
                }

                selectedNode = null;
                currentMode = Mode.NORMAL;
                updateStatusBar();
            }
        }

        private void selectNode(int x, int y) {
            Node clickedNode = getNodeAt(x, y);
            selectedNode = clickedNode;
            repaint();
        }

        private Node getNodeAt(int x, int y) {
            for (Node node : nodes) {
                double distance = Math.sqrt(Math.pow(node.x - x, 2) + Math.pow(node.y - y, 2));
                if (distance <= NODE_RADIUS) {
                    return node;
                }
            }
            return null;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw connections
            for (Connection conn : connections) {
                if (optimalConnections.contains(conn)) {
                    g2d.setColor(Color.GREEN);
                    g2d.setStroke(new BasicStroke(3));
                } else if (shortestPath.contains(conn)) {
                    g2d.setColor(Color.BLUE);
                    g2d.setStroke(new BasicStroke(3));
                } else {
                    g2d.setColor(Color.GRAY);
                    g2d.setStroke(new BasicStroke(1));
                }

                g2d.drawLine(conn.node1.x, conn.node1.y, conn.node2.x, conn.node2.y);

                // Draw connection info
                int midX = (conn.node1.x + conn.node2.x) / 2;
                int midY = (conn.node1.y + conn.node2.y) / 2;

                String info = String.format("$%.0f, %d Mbps", conn.cost, (int) conn.bandwidth);
                g2d.setColor(Color.BLACK);
                g2d.drawString(info, midX + 5, midY + 5);
            }

            // Reset stroke
            g2d.setStroke(new BasicStroke(1));

            // Draw nodes
            for (Node node : nodes) {
                if (node.type == NodeType.SERVER) {
                    g2d.setColor(Color.RED);
                } else {
                    g2d.setColor(Color.BLUE);
                }

                g2d.fill(new Ellipse2D.Double(node.x - NODE_RADIUS, node.y - NODE_RADIUS,
                        NODE_RADIUS * 2, NODE_RADIUS * 2));

                if (node == selectedNode) {
                    g2d.setColor(Color.BLACK);
                    g2d.setStroke(new BasicStroke(2));
                    g2d.draw(new Ellipse2D.Double(node.x - NODE_RADIUS - 2, node.y - NODE_RADIUS - 2,
                            (NODE_RADIUS + 2) * 2, (NODE_RADIUS + 2) * 2));
                }

                g2d.setColor(Color.WHITE);

                // Center the text
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(node.id);
                int textHeight = fm.getHeight();

                g2d.drawString(node.id, node.x - textWidth / 2, node.y + textHeight / 4);
            }
        }
    }

    // Data classes
    private enum NodeType {
        SERVER, CLIENT
    }

    private class Node {
        String id;
        int x, y;
        NodeType type;

        public Node(String id, int x, int y, NodeType type) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.type = type;
        }
    }

    private class Connection {
        Node node1, node2;
        double cost;
        double bandwidth;

        public Connection(Node node1, Node node2, double cost, double bandwidth) {
            this.node1 = node1;
            this.node2 = node2;
            this.cost = cost;
            this.bandwidth = bandwidth;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            NetworkOptimizationGUI app = new NetworkOptimizationGUI();
            app.setVisible(true);
        });
    }
}