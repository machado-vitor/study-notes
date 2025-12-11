import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

public class TimeLimiterPattern {
    private final TimeLimiter timeLimiter; // component that enforces timeouts
    private final ExecutorService executor; // thread pool to run async tasks
    private long serviceDelayMs = 500;

    public TimeLimiterPattern() {
        TimeLimiterConfig config = TimeLimiterConfig.custom()
            .timeoutDuration(Duration.ofSeconds(1)) // max 1 sec allowed per call
            .cancelRunningFuture(true) // if timeout, cancel the running task
            .build();

        this.timeLimiter = TimeLimiter.of("service", config);
        this.executor = Executors.newFixedThreadPool(4);
    }

    public String execute() {
        Callable<String> restrictedCall = TimeLimiter
            .decorateFutureSupplier(timeLimiter, () -> executor.submit(this::callService)); // submits callService() to the thread pool
// when we pass the lambda, java sees the expected parameter type is Supplier<Future<T>>, so it treats the lambda as a Supplier.
        try {
            return restrictedCall.call();
        } catch (TimeoutException e) {
            return fallback(e);
        } catch (Exception e) {
            return "[ERROR] " + e.getMessage();
        }
    }

    private String callService() {
        try {
            Thread.sleep(serviceDelayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Interrupted";
        }
        return "Success";
    }

    private String fallback(Exception e) {
        return "[FALLBACK] Operation timed out";
    }

    public void setServiceDelayMs(long delayMs) {
        this.serviceDelayMs = delayMs;
    }

    public void shutdown() {
        executor.shutdown();
    }

    public static void main(String[] args) {
        TimeLimiterPattern tl = new TimeLimiterPattern();

        System.out.println("TimeLimiter Pattern - Timeout after 1 second");
        System.out.println("-----------------------------------------------------------");

        System.out.println("\nPhase 1: Fast service (500ms delay) - should succeed");
        System.out.println("-----------------------------------------------------------");
        tl.setServiceDelayMs(500);
        for (int i = 1; i <= 3; i++) {
            long start = System.currentTimeMillis();
            String result = tl.execute();
            long elapsed = System.currentTimeMillis() - start;
            System.out.printf("Call %d: %s (took %dms)%n", i, result, elapsed);
        }

        System.out.println("\nPhase 2: Slow service (2000ms delay) - should timeout");
        System.out.println("-----------------------------------------------------------");
        tl.setServiceDelayMs(2000);
        for (int i = 1; i <= 3; i++) {
            long start = System.currentTimeMillis();
            String result = tl.execute();
            long elapsed = System.currentTimeMillis() - start;
            System.out.printf("Call %d: %s (took %dms)%n", i, result, elapsed);
        }

        System.out.println("\nPhase 3: Service recovered (500ms delay) - should succeed");
        System.out.println("-----------------------------------------------------------");
        tl.setServiceDelayMs(500);
        for (int i = 1; i <= 3; i++) {
            long start = System.currentTimeMillis();
            String result = tl.execute();
            long elapsed = System.currentTimeMillis() - start;
            System.out.printf("Call %d: %s (took %dms)%n", i, result, elapsed);
        }

        tl.shutdown();
    }
}
