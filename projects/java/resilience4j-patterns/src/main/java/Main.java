public class Main {
    public static void main(String[] args) throws InterruptedException {
        RemoteService service = new RemoteService();

        System.out.println("=== Circuit Breaker Pattern ===");
        CircuitBreakerPattern cbPattern = new CircuitBreakerPattern(service);
        for (int i = 0; i < 12; i++) {
            System.out.println("Call " + (i + 1) + ": " + cbPattern.execute() +
                             " | State: " + cbPattern.getState());
            Thread.sleep(100);
        }

        service.reset();
        System.out.println("\n=== Retry Pattern ===");
        RetryPattern retryPattern = new RetryPattern(service);
        System.out.println(retryPattern.execute());

        service.reset();
        System.out.println("\n=== Rate Limiter Pattern ===");
        RateLimiterPattern rlPattern = new RateLimiterPattern(service);
        for (int i = 0; i < 8; i++) {
            System.out.println("Call " + (i + 1) + ": " + rlPattern.execute() +
                             " | Available: " + rlPattern.getAvailablePermissions());
            Thread.sleep(50);
        }

        System.out.println("\n=== Bulkhead Pattern ===");
        BulkheadPattern bhPattern = new BulkheadPattern(service);
        for (int i = 0; i < 5; i++) {
            System.out.println("Call " + (i + 1) + ": " + bhPattern.execute() +
                             " | Available slots: " + bhPattern.getAvailableConcurrentCalls());
        }

        System.out.println("\n=== Time Limiter Pattern ===");
        TimeLimiterPattern tlPattern = new TimeLimiterPattern(service);
        System.out.println(tlPattern.execute());
        tlPattern.shutdown();
    }
}