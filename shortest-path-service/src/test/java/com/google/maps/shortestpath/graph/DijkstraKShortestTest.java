package com.google.maps.shortestpath.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for Yen's k-shortest paths algorithm on a diamond-shaped graph:
 *
 *        0
 *       / \
 *      1   2
 *       \ /
 *        3
 *
 * Edge weights (bidirectional via addEdge):
 *   0-1: 1.0, 0-2: 2.0, 1-3: 2.0, 2-3: 1.0
 *
 * Shortest path 0→1→3: 3.0
 * Second path   0→2→3: 3.0
 */
class DijkstraKShortestTest {

    private DijkstraKShortest algorithm;
    private Graph diamondGraph;

    @BeforeEach
    void setUp() {
        algorithm = new DijkstraKShortest();

        diamondGraph = new Graph();
        diamondGraph.addNode(new Graph.Node(0, 0.0, 0.0));
        diamondGraph.addNode(new Graph.Node(1, 1.0, 0.0));
        diamondGraph.addNode(new Graph.Node(2, 0.0, 1.0));
        diamondGraph.addNode(new Graph.Node(3, 1.0, 1.0));

        diamondGraph.addEdge(0, 1, 1.0);
        diamondGraph.addEdge(0, 2, 2.0);
        diamondGraph.addEdge(1, 3, 2.0);
        diamondGraph.addEdge(2, 3, 1.0);
    }

    @Test
    void findKShortestPaths_returnsSingleShortestPath() {
        List<DijkstraKShortest.Path> paths = algorithm.findKShortestPaths(diamondGraph, 0, 3, 1);

        assertThat(paths).hasSize(1);
        assertThat(paths.get(0).getTotalDistance()).isEqualTo(3.0);
        assertThat(paths.get(0).getNodeIds()).isEqualTo(List.of(0, 1, 3));
    }

    @Test
    void findKShortestPaths_returnsTwoPathsOrderedByDistance() {
        List<DijkstraKShortest.Path> paths = algorithm.findKShortestPaths(diamondGraph, 0, 3, 2);

        assertThat(paths).hasSize(2);

        // Both paths have distance 3.0
        assertThat(paths.get(0).getTotalDistance()).isEqualTo(3.0);
        assertThat(paths.get(1).getTotalDistance()).isEqualTo(3.0);

        // The two paths together must cover both routes
        assertThat(paths).anyMatch(p -> p.getNodeIds().equals(List.of(0, 1, 3)));
        assertThat(paths).anyMatch(p -> p.getNodeIds().equals(List.of(0, 2, 3)));
    }

    @Test
    void findKShortestPaths_requestMoreThanExist_returnsAvailable() {
        List<DijkstraKShortest.Path> paths = algorithm.findKShortestPaths(diamondGraph, 0, 3, 10);

        // Only 2 simple paths exist in this diamond
        assertThat(paths).hasSizeGreaterThanOrEqualTo(2);
        assertThat(paths).hasSizeLessThanOrEqualTo(10);
    }

    @Test
    void findKShortestPaths_noPathExists_returnsEmpty() {
        Graph disconnectedGraph = new Graph();
        disconnectedGraph.addNode(new Graph.Node(0, 0.0, 0.0));
        disconnectedGraph.addNode(new Graph.Node(1, 1.0, 0.0));
        // No edges — nodes are disconnected

        List<DijkstraKShortest.Path> paths = algorithm.findKShortestPaths(disconnectedGraph, 0, 1, 3);

        assertThat(paths).isEmpty();
    }

    @Test
    void findKShortestPaths_noDuplicatePaths() {
        List<DijkstraKShortest.Path> paths = algorithm.findKShortestPaths(diamondGraph, 0, 3, 5);

        // All returned path node lists must be unique
        long distinctCount = paths.stream()
                .map(DijkstraKShortest.Path::getNodeIds)
                .distinct()
                .count();

        assertThat(distinctCount).isEqualTo(paths.size());
    }
}
