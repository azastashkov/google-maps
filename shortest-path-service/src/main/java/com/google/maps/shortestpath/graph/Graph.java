package com.google.maps.shortestpath.graph;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {

    @Data
    @AllArgsConstructor
    public static class Node {
        private int id;
        private double latitude;
        private double longitude;
    }

    @Data
    @AllArgsConstructor
    public static class Edge {
        private int fromId;
        private int toId;
        private double distanceMeters;
    }

    private final Map<Integer, Node> nodes = new HashMap<>();
    private final Map<Integer, List<Edge>> adjacencyList = new HashMap<>();

    public void addNode(Node node) {
        nodes.put(node.getId(), node);
        adjacencyList.putIfAbsent(node.getId(), new ArrayList<>());
    }

    public void addEdge(int fromId, int toId, double distanceMeters) {
        adjacencyList.computeIfAbsent(fromId, k -> new ArrayList<>())
                .add(new Edge(fromId, toId, distanceMeters));
        adjacencyList.computeIfAbsent(toId, k -> new ArrayList<>())
                .add(new Edge(toId, fromId, distanceMeters));
    }

    public List<Edge> getNeighbors(int nodeId) {
        return adjacencyList.getOrDefault(nodeId, new ArrayList<>());
    }

    public Node getNode(int id) {
        return nodes.get(id);
    }

    public Node findNearestNode(double lat, double lng) {
        Node nearest = null;
        double minDist = Double.MAX_VALUE;
        for (Node node : nodes.values()) {
            double dist = haversineDistance(lat, lng, node.getLatitude(), node.getLongitude());
            if (dist < minDist) {
                minDist = dist;
                nearest = node;
            }
        }
        return nearest;
    }

    public static double haversineDistance(double lat1, double lng1, double lat2, double lng2) {
        final double R = 6_371_000.0; // Earth radius in meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
