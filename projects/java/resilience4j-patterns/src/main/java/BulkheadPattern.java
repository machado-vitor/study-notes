import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import java.time.Duration;
import java.util.function.Supplier;

public class BulkheadPattern {
    private final RemoteService service;
    private final Bulkhead bulkhead;

    public BulkheadPattern(RemoteService service) {
        this.service = service;

        BulkheadConfig config = BulkheadConfig.custom()
            .maxConcurrentCalls(3)
            .maxWaitDuration(Duration.ofMillis(100))
            .build();

        this.bulkhead = Bulkhead.of("serviceBulkhead", config);
    }

    public String execute() {
        Supplier<String> decoratedSupplier = Bulkhead
            .decorateSupplier(bulkhead, () -> service.reliableOperation());

        try {
            return decoratedSupplier.get();
        } catch (Exception e) {
            return "Bulkhead full: " + e.getMessage();
        }
    }

    public int getAvailableConcurrentCalls() {
        return bulkhead.getMetrics().getAvailableConcurrentCalls();
    }
}