import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class BulkheadPattern {
    private final Bulkhead bulkhead;

    public BulkheadPattern() {
        BulkheadConfig config = BulkheadConfig.custom()
            .maxConcurrentCalls(3)
            .maxWaitDuration(Duration.ofMillis(500))
            .build();

        this.bulkhead = Bulkhead.of("service", config);
    }

    public String execute() {
        Supplier<String> decoratedSupplier = Bulkhead
            .decorateSupplier(bulkhead, this::callService);

        try {
            return decoratedSupplier.get();
        } catch (BulkheadFullException e) {
            return fallback(e);
        }
    }

    private String callService() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "Success";
    }

    private String fallback(Exception e) {
        return "[FALLBACK] Bulkhead full - request rejected";
    }

    public String getMetrics() {
        var metrics = bulkhead.getMetrics();
        return String.format("available=%d, max=%d",
            metrics.getAvailableConcurrentCalls(),
            metrics.getMaxAllowedConcurrentCalls());
    }

    public static void main(String[] args) throws InterruptedException {
        BulkheadPattern bp = new BulkheadPattern();

        System.out.println("Bulkhead Pattern - Limiting concurrent calls to 3");
        System.out.println("-----------------------------------------------------------");
        System.out.println("Starting 6 concurrent requests (3 will be accepted, 3 rejected)");
        System.out.println();

        CompletableFuture<?>[] futures = new CompletableFuture[6];

        for (int i = 0; i < 6; i++) {
            final int callNumber = i + 1;
            futures[i] = CompletableFuture.runAsync(() -> {
                System.out.printf("Call %d: Starting | %s%n", callNumber, bp.getMetrics());
                String result = bp.execute();
                System.out.printf("Call %d: %s | %s%n", callNumber, result, bp.getMetrics());
            });
            Thread.sleep(100);
        }

        CompletableFuture.allOf(futures).join();

        System.out.println();
        System.out.println("-----------------------------------------------------------");
        System.out.println("All calls completed");
        System.out.println("Final metrics: " + bp.getMetrics());
    }
}