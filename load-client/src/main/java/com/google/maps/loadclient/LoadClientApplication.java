package com.google.maps.loadclient;

import com.google.maps.loadclient.scenario.BaseScenario;
import com.google.maps.loadclient.scenario.GeocodingScenario;
import com.google.maps.loadclient.scenario.LocationUpdateScenario;
import com.google.maps.loadclient.scenario.MapTileScenario;
import com.google.maps.loadclient.scenario.NavigationScenario;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executors;

public class LoadClientApplication {

    public static void main(String[] args) throws InterruptedException {
        int durationSeconds = Integer.parseInt(
                System.getenv().getOrDefault("LOAD_TEST_DURATION_SECONDS", "60")
        );
        String baseUrl = System.getenv().getOrDefault("TARGET_BASE_URL", "http://nginx:80");

        System.out.println("Starting load test against: " + baseUrl);
        System.out.println("Duration: " + durationSeconds + " seconds");
        System.out.println();

        HttpClient client = HttpClient.newBuilder()
                .executor(Executors.newVirtualThreadPerTaskExecutor())
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        List<BaseScenario> scenarios = List.of(
                new LocationUpdateScenario(),
                new NavigationScenario(),
                new GeocodingScenario(),
                new MapTileScenario()
        );

        for (BaseScenario scenario : scenarios) {
            scenario.start(client, baseUrl);
        }

        System.out.println("All scenarios started. Running for " + durationSeconds + " seconds...");
        Thread.sleep(Duration.ofSeconds(durationSeconds));

        System.out.println("Stopping scenarios...");
        for (BaseScenario scenario : scenarios) {
            scenario.stop();
        }

        System.out.println();
        System.out.println("=== Load Test Results ===");
        System.out.println();
        for (BaseScenario scenario : scenarios) {
            scenario.printStats();
        }
    }
}
