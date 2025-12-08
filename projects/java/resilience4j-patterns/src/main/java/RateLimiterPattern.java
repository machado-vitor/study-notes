import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import java.time.Duration;
import java.util.function.Supplier;

public class RateLimiterPattern {
    private final RemoteService service;
    private final RateLimiter rateLimiter;

    public RateLimiterPattern(RemoteService service) {
        this.service = service;

        RateLimiterConfig config = RateLimiterConfig.custom()
            .limitForPeriod(5)
            .limitRefreshPeriod(Duration.ofSeconds(1))
            .timeoutDuration(Duration.ofMillis(100))
            .build();

        this.rateLimiter = RateLimiter.of("apiRateLimiter", config);
    }

    public String execute() {
        Supplier<String> decoratedSupplier = RateLimiter
            .decorateSupplier(rateLimiter, () -> service.reliableOperation());

        try {
            return decoratedSupplier.get();
        } catch (Exception e) {
            return "Rate limit exceeded: " + e.getMessage();
        }
    }

    public int getAvailablePermissions() {
        return rateLimiter.getMetrics().getAvailablePermissions();
    }
}