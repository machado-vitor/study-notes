import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import java.time.Duration;
import java.util.function.Supplier;

public class CircuitBreakerPattern {
    private final RemoteService service;
    private final CircuitBreaker circuitBreaker;

    public CircuitBreakerPattern(RemoteService service) {
        this.service = service;

        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
            .failureRateThreshold(50)
            .waitDurationInOpenState(Duration.ofSeconds(5))
            .slidingWindowSize(10)
            .minimumNumberOfCalls(5)
            .build();

        this.circuitBreaker = CircuitBreaker.of("externalService", config);
    }

    public String execute() {
        Supplier<String> decoratedSupplier = CircuitBreaker
            .decorateSupplier(circuitBreaker, () -> {
                try {
                    return service.callExternalAPI();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

        try {
            return decoratedSupplier.get();
        } catch (Exception e) {
            return "Circuit breaker fallback: " + e.getMessage();
        }
    }

    public String getState() {
        return circuitBreaker.getState().toString();
    }
}