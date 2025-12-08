# Resilience4j Patterns

Java 25 project demonstrating Resilience4j fault tolerance patterns.

## Patterns Implemented

- **CircuitBreaker**: Prevents cascading failures by opening circuit after failure threshold
- **Retry**: Automatically retries failed operations with configurable attempts and delays
- **RateLimiter**: Controls rate of requests to prevent system overload
- **Bulkhead**: Limits concurrent executions to prevent resource exhaustion
- **TimeLimiter**: Sets timeout for operations to prevent hanging calls

## Build and Run

```bash
mvn clean install
mvn exec:java
```

## Project Structure

```
src/main/java/
├── RemoteService.java          - Simulates external service with failures
├── CircuitBreakerPattern.java  - Circuit breaker implementation
├── RetryPattern.java           - Retry mechanism
├── RateLimiterPattern.java     - Rate limiting
├── BulkheadPattern.java        - Bulkhead isolation
├── TimeLimiterPattern.java     - Timeout handling
└── Main.java                   - Runs all patterns
```

## Dependencies

- Resilience4j 2.2.0
- Java 25