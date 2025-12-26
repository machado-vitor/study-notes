import io.github.resilience4j.bulkhead.ThreadPoolBulkhead;
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadConfig;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class ThreadPoolBulkheadPattern {
    private final ThreadPoolBulkhead bulkhead;

    public ThreadPoolBulkheadPattern() {
        ThreadPoolBulkheadConfig config = ThreadPoolBulkheadConfig.custom()
            .maxThreadPoolSize(3)
            .coreThreadPoolSize(2)
            .queueCapacity(2)
            .keepAliveDuration(Duration.ofMillis(500))
            .build();

        this.bulkhead = ThreadPoolBulkhead.of("service", config);
    }

    public CompletionStage<String> execute() {
        return bulkhead.executeSupplier(this::callService)
            .exceptionally(this::fallback);
    }

    private String callService() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "Success";
    }

    private String fallback(Throwable t) {
        return "[FALLBACK] " + t.getMessage();
    }

    public String getMetrics() {
        var metrics = bulkhead.getMetrics();
        return String.format("threads=%d, queue=%d, max=%d, queueCapacity=%d",
            metrics.getActiveThreadCount(),
            metrics.getQueueDepth(),
            metrics.getMaximumThreadPoolSize(),
            metrics.getQueueCapacity());
    }

    public static void main(String[] args) throws InterruptedException {
        ThreadPoolBulkheadPattern bp = new ThreadPoolBulkheadPattern();

        System.out.println("ThreadPool Bulkhead Pattern");
        System.out.println("Config: maxThreads=3, coreThreads=2, queueCapacity=2");
        System.out.println("-----------------------------------------------------------");
        System.out.println("Starting 8 concurrent requests");
        System.out.println("(3 threads + 2 queue = 5 accepted, 3 rejected)");
        System.out.println();

        CompletableFuture<?>[] futures = new CompletableFuture[8];

        for (int i = 0; i < 8; i++) {
            final int callNumber = i + 1;
            System.out.printf("Call %d: Submitting | %s%n", callNumber, bp.getMetrics());

            futures[i] = bp.execute()
                .thenAccept(result ->
                    System.out.printf("Call %d: %s | %s%n", callNumber, result, bp.getMetrics()))
                .toCompletableFuture();

            Thread.sleep(50);
        }

        CompletableFuture.allOf(futures).join();

        System.out.println();
        System.out.println("-----------------------------------------------------------");
        System.out.println("All calls completed");
        System.out.println("Final metrics: " + bp.getMetrics());

        bp.close();
    }

    public void close() {
        try {
            bulkhead.close();
        } catch (Exception ignored) {
        }
    }
}