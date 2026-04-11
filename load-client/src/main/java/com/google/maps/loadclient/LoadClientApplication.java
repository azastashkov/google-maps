package com.google.maps.loadclient;

import com.google.maps.loadclient.scenario.BaseScenario;
import com.google.maps.loadclient.scenario.GeocodingScenario;
import com.google.maps.loadclient.scenario.LocationUpdateScenario;
import com.google.maps.loadclient.scenario.MapTileScenario;
import com.google.maps.loadclient.scenario.NavigationScenario;
import lombok.extern.slf4j.Slf4j;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executors;

@Slf4j
public class LoadClientApplication {

    public static void main(String[] args) throws InterruptedException {
        int durationSeconds = Integer.parseInt(
                System.getenv().getOrDefault("LOAD_TEST_DURATION_SECONDS", "60")
        );
        String baseUrl = System.getenv().getOrDefault("TARGET_BASE_URL", "http://nginx:80");

        log.info("Starting load test against: {}", baseUrl);
        log.info("Duration: {} seconds", durationSeconds);

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

        log.info("All scenarios started. Running for {} seconds...", durationSeconds);
        Thread.sleep(Duration.ofSeconds(durationSeconds));

        log.info("Stopping scenarios...");
        for (BaseScenario scenario : scenarios) {
            scenario.stop();
        }

        log.info("=== Load Test Results ===");
        for (BaseScenario scenario : scenarios) {
            scenario.printStats();
        }
    }
}
