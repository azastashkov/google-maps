package com.google.maps.shortestpath.graph;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class GraphGenerator {

    private static final int ROWS = 25;
    private static final int COLS = 20;
    private static final double BASE_LAT = 40.70;
    private static final double BASE_LNG = -74.02;
    private static final double SPACING = 0.005;

    @Getter
    private final Graph graph;

    public GraphGenerator() {
        this.graph = new Graph();
        generateGraph();
    }

    private void generateGraph() {
        // Add all nodes
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                int id = i * COLS + j;
                double lat = BASE_LAT + i * SPACING;
                double lng = BASE_LNG + j * SPACING;
                graph.addNode(new Graph.Node(id, lat, lng));
            }
        }

        // Add horizontal and vertical edges, plus diagonal shortcuts
        Random random = new Random(42);
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                int currentId = i * COLS + j;
                Graph.Node current = graph.getNode(currentId);

                // Horizontal edge: (i,j) -> (i,j+1)
                if (j + 1 < COLS) {
                    int rightId = i * COLS + (j + 1);
                    Graph.Node right = graph.getNode(rightId);
                    double dist = Graph.haversineDistance(
                            current.getLatitude(), current.getLongitude(),
                            right.getLatitude(), right.getLongitude());
                    graph.addEdge(currentId, rightId, dist);
                }

                // Vertical edge: (i,j) -> (i+1,j)
                if (i + 1 < ROWS) {
                    int downId = (i + 1) * COLS + j;
                    Graph.Node down = graph.getNode(downId);
                    double dist = Graph.haversineDistance(
                            current.getLatitude(), current.getLongitude(),
                            down.getLatitude(), down.getLongitude());
                    graph.addEdge(currentId, downId, dist);
                }

                // Diagonal shortcut: (i,j) -> (i+1,j+1) with 30% probability
                if (i + 1 < ROWS && j + 1 < COLS && random.nextDouble() < 0.30) {
                    int diagId = (i + 1) * COLS + (j + 1);
                    Graph.Node diag = graph.getNode(diagId);
                    double dist = Graph.haversineDistance(
                            current.getLatitude(), current.getLongitude(),
                            diag.getLatitude(), diag.getLongitude());
                    graph.addEdge(currentId, diagId, dist);
                }
            }
        }
    }

}
