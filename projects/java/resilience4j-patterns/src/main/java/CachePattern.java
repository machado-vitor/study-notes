import io.github.resilience4j.cache.Cache;
import com.github.benmanes.caffeine.jcache.spi.CaffeineCachingProvider;
import javax.cache.CacheManager;
import javax.cache.configuration.MutableConfiguration;
import java.util.concurrent.atomic.AtomicInteger;

public class CachePattern {
    private final Cache<String, String> cache;
    private final javax.cache.Cache<String, String> jCache;
    // Resilience4j requires JCache, it is like a JDBC,
    // it defines how to interact with caches, but needs the actual implementation. Caffeine is the actual cache.
    private final AtomicInteger serviceCallCount = new AtomicInteger(0);
    private final AtomicInteger cacheHits = new AtomicInteger(0);
    private final AtomicInteger cacheMisses = new AtomicInteger(0);

    public CachePattern() {
        CaffeineCachingProvider provider = new CaffeineCachingProvider();
        CacheManager cacheManager = provider.getCacheManager();

        MutableConfiguration<String, String> configuration = new MutableConfiguration<String, String>()
            .setTypes(String.class, String.class)
            .setStoreByValue(false);

        this.jCache = cacheManager.createCache("serviceCache", configuration);
        this.cache = Cache.of(jCache);

        cache.getEventPublisher()
            .onCacheHit(event -> cacheHits.incrementAndGet())
            .onCacheMiss(event -> cacheMisses.incrementAndGet());
    }

    public String execute(String key) {
        try {
            String cachedValue = jCache.get(key);
            if (cachedValue != null) {
                cacheHits.incrementAndGet();
                return cachedValue;
            }
            cacheMisses.incrementAndGet();
            String result = callService(key);
            jCache.put(key, result);
            return result;
        } catch (Exception e) {
            return fallback(e);
        }
    }

    private String callService(String key) {
        serviceCallCount.incrementAndGet();
        return "Response for " + key + " [call #" + serviceCallCount.get() + "]";
    }

    private String fallback(Exception e) {
        return "[FALLBACK] Cache error: " + e.getMessage();
    }

    public void clearCache() {
        jCache.clear();
    }

    public String getMetrics() {
        return String.format("hits=%d, misses=%d, serviceCalls=%d",
            cacheHits.get(), cacheMisses.get(), serviceCallCount.get());
    }

    public static void main(String[] args) {
        CachePattern cp = new CachePattern();

        System.out.println("Cache Pattern - Caching service responses");
        System.out.println("-----------------------------------------------------------");

        System.out.println("\nPhase 1: First calls - all cache misses");
        System.out.println("-----------------------------------------------------------");
        String[] keys = {"user-1", "user-2", "user-3"};
        for (String key : keys) {
            String result = cp.execute(key);
            System.out.printf("Key '%s': %s | %s%n", key, result, cp.getMetrics());
        }

        System.out.println("\nPhase 2: Repeat calls - all cache hits (no service calls)");
        System.out.println("-----------------------------------------------------------");
        for (String key : keys) {
            String result = cp.execute(key);
            System.out.printf("Key '%s': %s | %s%n", key, result, cp.getMetrics());
        }

        System.out.println("\nPhase 3: New key - cache miss, then hit");
        System.out.println("-----------------------------------------------------------");
        for (int i = 0; i < 2; i++) {
            String result = cp.execute("user-4");
            System.out.printf("Key 'user-4': %s | %s%n", result, cp.getMetrics());
        }

        System.out.println("\nPhase 4: Clear cache and retry - all misses again");
        System.out.println("-----------------------------------------------------------");
        cp.clearCache();
        System.out.println("Cache cleared!");
        for (String key : keys) {
            String result = cp.execute(key);
            System.out.printf("Key '%s': %s | %s%n", key, result, cp.getMetrics());
        }
    }
}