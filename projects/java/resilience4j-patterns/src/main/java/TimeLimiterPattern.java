import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;

public class TimeLimiterPattern {
    private final RemoteService service;
    private final TimeLimiter timeLimiter;
    private final ScheduledExecutorService scheduler;

    public TimeLimiterPattern(RemoteService service) {
        this.service = service;
        this.scheduler = Executors.newScheduledThreadPool(2);

        TimeLimiterConfig config = TimeLimiterConfig.custom()
            .timeoutDuration(Duration.ofSeconds(2))
            .build();

        this.timeLimiter = TimeLimiter.of("timeoutService", config);
    }

    public String execute() {
        Supplier<CompletableFuture<String>> futureSupplier = () ->
            CompletableFuture.supplyAsync(() -> {
                try {
                    return service.slowOperation();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }, scheduler);

        try {
            return timeLimiter.executeFutureSupplier(futureSupplier);
        } catch (Exception e) {
            return "Operation timed out: " + e.getMessage();
        }
    }

    public void shutdown() {
        scheduler.shutdown();
    }
}