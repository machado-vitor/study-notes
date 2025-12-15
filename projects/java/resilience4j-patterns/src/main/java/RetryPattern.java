import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class RetryPattern {
    private final Retry retry;
    private boolean shouldFail = true;
    private final AtomicInteger attemptCounter = new AtomicInteger(0);

    public RetryPattern() {
        RetryConfig config = RetryConfig.custom()
            .maxAttempts(4)
            .waitDuration(Duration.ofMillis(500))
            .retryExceptions(RuntimeException.class) // these are the matching exceptions
            .build();

        this.retry = Retry.of("service", config);
    }

    public String execute() {
        attemptCounter.set(0);
        Supplier<String> decoratedSupplier = Retry
            .decorateSupplier(retry, this::callService);

        try {
            return decoratedSupplier.get();
        } catch (Exception e) {
            return fallback(e);
        }
    }

    private String callService() {
        int attempt = attemptCounter.incrementAndGet();
        System.out.printf("  -> Attempt %d...%n", attempt);
        if (shouldFail) {
            throw new RuntimeException("Service unavailable");
        }
        return "Success";
    }

    private String fallback(Exception e) {
        return "[FALLBACK] All retries exhausted: " + e.getMessage();
    }

    public void setServiceHealthy(boolean healthy) {
        this.shouldFail = !healthy;
    }

    public void setFailUntilAttempt(int successAttempt) {
        this.shouldFail = true;
        attemptCounter.set(0);
        Supplier<String> decoratedSupplier = Retry
            .decorateSupplier(retry, () -> {
                int attempt = attemptCounter.incrementAndGet();
                System.out.printf("  -> Attempt %d...%n", attempt);
                if (attempt < successAttempt) {
                    throw new RuntimeException("Service unavailable");
                }
                return "Success";
            });

        try {
            System.out.println("Result: " + decoratedSupplier.get());
        } catch (Exception e) {
            System.out.println("Result: " + fallback(e));
        }
    }

    public String getMetrics() {
        var metrics = retry.getMetrics();
        return String.format("successful=%d, failed=%d",
            metrics.getNumberOfSuccessfulCallsWithoutRetryAttempt() + metrics.getNumberOfSuccessfulCallsWithRetryAttempt(),
            metrics.getNumberOfFailedCallsWithoutRetryAttempt() + metrics.getNumberOfFailedCallsWithRetryAttempt());
    }

    public static void main(String[] args) {
        RetryPattern rp = new RetryPattern();

        System.out.println("Retry Pattern - Max 4 attempts with 500ms delay");
        System.out.println("-----------------------------------------------------------");

        System.out.println("\nPhase 1: Service healthy - no retries needed");
        System.out.println("-----------------------------------------------------------");
        rp.setServiceHealthy(true);
        for (int i = 1; i <= 2; i++) {
            System.out.printf("Call %d:%n", i);
            System.out.printf("Result: %s | %s%n%n", rp.execute(), rp.getMetrics());
        }

        System.out.println("Phase 2: Service failing - will retry and eventually fail");
        System.out.println("-----------------------------------------------------------");
        rp.setServiceHealthy(false);
        System.out.println("Call 1:");
        System.out.printf("Result: %s | %s%n%n", rp.execute(), rp.getMetrics());

        System.out.println("Phase 3: Service recovers on 3rd attempt - retry succeeds");
        System.out.println("-----------------------------------------------------------");
        System.out.println("Call 1 (will succeed on attempt 3):");
        rp.setFailUntilAttempt(3);
        System.out.println(rp.getMetrics());
    }
}