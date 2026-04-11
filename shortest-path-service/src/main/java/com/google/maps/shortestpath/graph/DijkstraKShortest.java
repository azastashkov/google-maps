package com.google.maps.shortestpath.graph;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

@Component
public class DijkstraKShortest {

    @Data
    @AllArgsConstructor
    public static class Path {
        private List<Integer> nodeIds;
        private double totalDistance;
    }

    public List<Path> findKShortestPaths(Graph graph, int source, int target, int k) {
        List<Path> result = new ArrayList<>();

        // Find the first shortest path using Dijkstra
        Path firstPath = dijkstra(graph, source, target, Collections.emptySet(), Collections.emptySet());
        if (firstPath == null) {
            return result;
        }
        result.add(firstPath);

        // Candidate paths priority queue (ordered by total distance)
        PriorityQueue<Path> candidates = new PriorityQueue<>(
                Comparator.comparingDouble(Path::getTotalDistance));

        // Track candidate path node lists to avoid duplicates
        Set<List<Integer>> seen = new HashSet<>();
        seen.add(firstPath.getNodeIds());

        for (int iteration = 1; iteration < k; iteration++) {
            Path prevPath = result.get(result.size() - 1);
            List<Integer> prevNodes = prevPath.getNodeIds();

            // Iterate over each spur node in the previous best path
            for (int spurIdx = 0; spurIdx < prevNodes.size() - 1; spurIdx++) {
                int spurNode = prevNodes.get(spurIdx);
                List<Integer> rootPath = new ArrayList<>(prevNodes.subList(0, spurIdx + 1));

                // Edges to exclude: edges used by already-found paths that share the same root
                Set<String> excludedEdges = new HashSet<>();
                for (Path p : result) {
                    List<Integer> pNodes = p.getNodeIds();
                    if (pNodes.size() > spurIdx && pNodes.subList(0, spurIdx + 1).equals(rootPath)) {
                        int nextNode = pNodes.get(spurIdx + 1);
                        excludedEdges.add(edgeKey(spurNode, nextNode));
                    }
                }
                // Also check current candidates for same root
                for (Path p : candidates) {
                    List<Integer> pNodes = p.getNodeIds();
                    if (pNodes.size() > spurIdx && pNodes.subList(0, spurIdx + 1).equals(rootPath)) {
                        int nextNode = pNodes.get(spurIdx + 1);
                        excludedEdges.add(edgeKey(spurNode, nextNode));
                    }
                }

                // Nodes to exclude: all nodes in the root path except the spur node itself
                Set<Integer> excludedNodes = new HashSet<>(rootPath.subList(0, rootPath.size() - 1));

                // Find spur path from spurNode to target
                Path spurPath = dijkstra(graph, spurNode, target, excludedNodes, excludedEdges);

                if (spurPath != null) {
                    // Build total path = root + spur (excluding the duplicate spur node at index 0)
                    List<Integer> totalNodes = new ArrayList<>(rootPath);
                    List<Integer> spurNodes = spurPath.getNodeIds();
                    totalNodes.addAll(spurNodes.subList(1, spurNodes.size()));

                    if (!seen.contains(totalNodes)) {
                        double rootCost = computeRootCost(graph, rootPath);
                        double totalDist = rootCost + spurPath.getTotalDistance();
                        Path candidate = new Path(totalNodes, totalDist);
                        candidates.add(candidate);
                        seen.add(totalNodes);
                    }
                }
            }

            if (candidates.isEmpty()) {
                break;
            }
            result.add(candidates.poll());
        }

        return result;
    }

    /**
     * Standard Dijkstra from source to target with optional node/edge exclusions.
     */
    Path dijkstra(Graph graph, int source, int target,
                  Set<Integer> excludedNodes, Set<String> excludedEdges) {
        Map<Integer, Double> dist = new HashMap<>();
        Map<Integer, Integer> prev = new HashMap<>();
        // PQ entry: [nodeId, distance as double bits stored in long — we use a wrapper record]
        PriorityQueue<double[]> pq = new PriorityQueue<>(Comparator.comparingDouble(a -> a[1]));

        dist.put(source, 0.0);
        pq.offer(new double[]{source, 0.0});

        while (!pq.isEmpty()) {
            double[] curr = pq.poll();
            int u = (int) curr[0];
            double d = curr[1];

            double bestDist = dist.getOrDefault(u, Double.MAX_VALUE);
            if (d > bestDist) {
                continue;
            }

            if (u == target) {
                return reconstructPath(prev, source, target, dist.get(target));
            }

            for (Graph.Edge edge : graph.getNeighbors(u)) {
                int v = edge.getToId();
                if (excludedNodes.contains(v)) {
                    continue;
                }
                if (excludedEdges.contains(edgeKey(u, v))) {
                    continue;
                }
                double newDist = bestDist + edge.getDistanceMeters();
                if (newDist < dist.getOrDefault(v, Double.MAX_VALUE)) {
                    dist.put(v, newDist);
                    prev.put(v, u);
                    pq.offer(new double[]{v, newDist});
                }
            }
        }
        return null;
    }

    private Path reconstructPath(Map<Integer, Integer> prev, int source, int target, double distance) {
        List<Integer> path = new ArrayList<>();
        int current = target;
        while (current != source) {
            path.add(current);
            Integer p = prev.get(current);
            if (p == null) {
                return null;
            }
            current = p;
        }
        path.add(source);
        Collections.reverse(path);
        return new Path(path, distance);
    }

    double computeRootCost(Graph graph, List<Integer> rootPath) {
        double cost = 0.0;
        for (int i = 0; i < rootPath.size() - 1; i++) {
            int from = rootPath.get(i);
            int to = rootPath.get(i + 1);
            for (Graph.Edge edge : graph.getNeighbors(from)) {
                if (edge.getToId() == to) {
                    cost += edge.getDistanceMeters();
                    break;
                }
            }
        }
        return cost;
    }

    private String edgeKey(int from, int to) {
        return from + "->" + to;
    }
}
