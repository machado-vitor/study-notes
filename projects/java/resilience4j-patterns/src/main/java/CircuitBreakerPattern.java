import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import java.time.Duration;
import java.util.function.Supplier;

public class CircuitBreakerPattern {
    private final CircuitBreaker circuitBreaker;
    private boolean shouldFail = true;

    public CircuitBreakerPattern() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
            .failureRateThreshold(50)
            .waitDurationInOpenState(Duration.ofSeconds(3))
            .slidingWindowSize(4)
            .minimumNumberOfCalls(4)
            .permittedNumberOfCallsInHalfOpenState(2)
            .automaticTransitionFromOpenToHalfOpenEnabled(true)
            .build();

        this.circuitBreaker = CircuitBreaker.of("service", config);
    }

    public String execute() {
        Supplier<String> decoratedSupplier = CircuitBreaker
            .decorateSupplier(circuitBreaker, this::callService);

        try {
            return decoratedSupplier.get();
        } catch (Exception e) {
            return fallback(e);
        }
    }

    private String callService() {
        if (shouldFail) {
            throw new RuntimeException("Service unavailable");
        }
        return "Success";
    }

    private String fallback(Exception e) {
        if (circuitBreaker.getState() == CircuitBreaker.State.OPEN) {
            return "[FALLBACK] Circuit is OPEN - returning cached response";
        }
        if (circuitBreaker.getState() == CircuitBreaker.State.HALF_OPEN) {
            return "[FALLBACK] Circuit is HALF_OPEN - test call failed";
        }
        return "[FALLBACK] Service error: " + e.getMessage();
    }

    public void setServiceHealthy(boolean healthy) {
        this.shouldFail = !healthy;
    }

    public String getState() {
        return circuitBreaker.getState().toString();
    }

    public String getMetrics() {
        var metrics = circuitBreaker.getMetrics();
        return String.format("failures=%d, success=%d, rate=%.0f%%",
            metrics.getNumberOfFailedCalls(),
            metrics.getNumberOfSuccessfulCalls(),
            metrics.getFailureRate());
    }

    public static void main(String[] args) throws InterruptedException {
        CircuitBreakerPattern cb = new CircuitBreakerPattern();
        cb.setServiceHealthy(true);

        System.out.println("Phase 1: Service healthy - normal operation");
        System.out.println("-----------------------------------------------------------");
        for (int i = 1; i <= 4; i++) {
            System.out.printf("Call %d: %s | State: %s | %s%n",
                i, cb.execute(), cb.getState(), cb.getMetrics());
            Thread.sleep(200);
        }

        System.out.println("\nPhase 2: Service starts failing - circuit will open");
        System.out.println("-----------------------------------------------------------");
        cb.setServiceHealthy(false);
        for (int i = 1; i <= 6; i++) {
            System.out.printf("Call %d: %s | State: %s | %s%n",
                i, cb.execute(), cb.getState(), cb.getMetrics());
            Thread.sleep(200);
        }

        System.out.println("\nPhase 3: Waiting for HALF_OPEN state (3 seconds)...");
        System.out.println("-----------------------------------------------------------");
        for (int i = 0; i < 4; i++) {
            Thread.sleep(1000);
            System.out.printf("Waiting... State: %s%n", cb.getState());
        }

        System.out.println("\nPhase 4: Service still failing - circuit will reopen");
        System.out.println("-----------------------------------------------------------");
        for (int i = 1; i <= 3; i++) {
            System.out.printf("Call %d: %s | State: %s | %s%n",
                i, cb.execute(), cb.getState(), cb.getMetrics());
            Thread.sleep(200);
        }

        System.out.println("\nPhase 5: Waiting for HALF_OPEN again (3 seconds)...");
        System.out.println("-----------------------------------------------------------");
        for (int i = 0; i < 4; i++) {
            Thread.sleep(1000);
            System.out.printf("Waiting... State: %s%n", cb.getState());
        }

        System.out.println("\nPhase 6: Service recovered - circuit will close");
        System.out.println("-----------------------------------------------------------");
        cb.setServiceHealthy(true);
        for (int i = 1; i <= 4; i++) {
            System.out.printf("Call %d: %s | State: %s | %s%n",
                i, cb.execute(), cb.getState(), cb.getMetrics());
            Thread.sleep(200);
        }
    }
}
