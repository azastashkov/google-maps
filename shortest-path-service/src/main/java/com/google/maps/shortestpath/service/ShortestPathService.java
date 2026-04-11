package com.google.maps.shortestpath.service;

import com.google.maps.shortestpath.dto.Coordinate;
import com.google.maps.shortestpath.dto.PathResult;
import com.google.maps.shortestpath.dto.ShortestPathsResponse;
import com.google.maps.shortestpath.graph.DijkstraKShortest;
import com.google.maps.shortestpath.graph.Graph;
import com.google.maps.shortestpath.graph.GraphGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShortestPathService {

    private final GraphGenerator graphGenerator;
    private final DijkstraKShortest dijkstraKShortest;

    public ShortestPathsResponse findShortestPaths(double originLat, double originLng,
                                                    double destLat, double destLng, int k) {
        Graph graph = graphGenerator.getGraph();

        Graph.Node originNode = graph.findNearestNode(originLat, originLng);
        Graph.Node destNode = graph.findNearestNode(destLat, destLng);

        List<DijkstraKShortest.Path> paths = dijkstraKShortest.findKShortestPaths(
                graph, originNode.getId(), destNode.getId(), k);

        List<PathResult> pathResults = paths.stream()
                .map(path -> {
                    List<Coordinate> waypoints = path.getNodeIds().stream()
                            .map(nodeId -> {
                                Graph.Node node = graph.getNode(nodeId);
                                return new Coordinate(node.getLatitude(), node.getLongitude());
                            })
                            .collect(Collectors.toList());
                    return new PathResult(path.getTotalDistance(), waypoints);
                })
                .collect(Collectors.toList());

        return new ShortestPathsResponse(pathResults);
    }
}
