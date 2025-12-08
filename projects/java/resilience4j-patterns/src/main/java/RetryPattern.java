import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import java.time.Duration;
import java.util.function.Supplier;

public class RetryPattern {
    private final RemoteService service;
    private final Retry retry;

    public RetryPattern(RemoteService service) {
        this.service = service;

        RetryConfig config = RetryConfig.custom()
            .maxAttempts(5)
            .waitDuration(Duration.ofMillis(500))
            .build();

        this.retry = Retry.of("serviceRetry", config);
    }

    public String execute() {
        Supplier<String> decoratedSupplier = Retry
            .decorateSupplier(retry, () -> {
                try {
                    return service.callExternalAPI();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

        try {
            return decoratedSupplier.get();
        } catch (Exception e) {
            return "All retry attempts failed: " + e.getMessage();
        }
    }

    public long getAttempts() {
        return retry.getMetrics().getNumberOfSuccessfulCallsWithoutRetryAttempt() +
               retry.getMetrics().getNumberOfSuccessfulCallsWithRetryAttempt() +
               retry.getMetrics().getNumberOfFailedCallsWithoutRetryAttempt() +
               retry.getMetrics().getNumberOfFailedCallsWithRetryAttempt();
    }
}