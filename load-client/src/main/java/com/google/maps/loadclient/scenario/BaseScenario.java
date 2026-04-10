package com.google.maps.loadclient.scenario;

import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

public abstract class BaseScenario {

    protected final String name;
    protected final int threadCount;
    protected volatile boolean running;

    protected final AtomicLong totalRequests = new AtomicLong(0);
    protected final AtomicLong successCount = new AtomicLong(0);
    protected final AtomicLong failureCount = new AtomicLong(0);
    protected final ConcurrentLinkedQueue<Long> latencies = new ConcurrentLinkedQueue<>();

    private final List<Thread> threads = new ArrayList<>();

    protected BaseScenario(String name, int threadCount) {
        this.name = name;
        this.threadCount = threadCount;
    }

    public void start(HttpClient client, String baseUrl) {
        running = true;
        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            Thread t = Thread.ofVirtual()
                    .name(name + "-thread-" + i)
                    .start(() -> {
                        while (running) {
                            try {
                                execute(client, baseUrl, threadIndex);
                                long delay = delayMs();
                                if (delay > 0) {
                                    Thread.sleep(delay);
                                }
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                break;
                            } catch (Exception e) {
                                failureCount.incrementAndGet();
                                totalRequests.incrementAndGet();
                            }
                        }
                    });
            threads.add(t);
        }
    }

    public void stop() {
        running = false;
        for (Thread t : threads) {
            t.interrupt();
        }
        for (Thread t : threads) {
            try {
                t.join(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void printStats() {
        List<Long> sorted = new ArrayList<>(latencies);
        Collections.sort(sorted);

        long total = totalRequests.get();
        long success = successCount.get();
        long failure = failureCount.get();

        double avg = 0;
        long p95 = 0;
        long p99 = 0;

        if (!sorted.isEmpty()) {
            long sum = 0;
            for (long l : sorted) {
                sum += l;
            }
            avg = (double) sum / sorted.size();
            p95 = sorted.get((int) Math.min(sorted.size() - 1, Math.ceil(sorted.size() * 0.95) - 1));
            p99 = sorted.get((int) Math.min(sorted.size() - 1, Math.ceil(sorted.size() * 0.99) - 1));
        }

        System.out.printf("=== %s ===%n", name);
        System.out.printf("  Total Requests : %d%n", total);
        System.out.printf("  Success        : %d%n", success);
        System.out.printf("  Failure        : %d%n", failure);
        System.out.printf("  Avg Latency    : %.2f ms%n", avg);
        System.out.printf("  P95 Latency    : %d ms%n", p95);
        System.out.printf("  P99 Latency    : %d ms%n", p99);
        System.out.println();
    }

    protected abstract void execute(HttpClient client, String baseUrl, int threadIndex) throws Exception;

    protected abstract long delayMs();
}
