package org.erp_microservices.peopleandorganizations.api.infrastructure.security;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class RateLimiter {

    private final int limit;
    private final int windowSeconds;
    private final Map<String, WindowCounter> counters = new ConcurrentHashMap<>();
    private final ScheduledExecutorService cleanupExecutor = Executors.newSingleThreadScheduledExecutor();

    public RateLimiter(int limit, int windowSeconds) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be positive");
        }
        if (windowSeconds <= 0) {
            throw new IllegalArgumentException("Window must be positive");
        }

        this.limit = limit;
        this.windowSeconds = windowSeconds;

        // Schedule cleanup every minute
        cleanupExecutor.scheduleAtFixedRate(this::cleanup, 1, 1, TimeUnit.MINUTES);
    }

    public boolean allowRequest(String clientId) {
        WindowCounter counter = counters.compute(clientId, (key, existing) -> {
            long now = getCurrentTimeMillis();
            if (existing == null || existing.isExpired(now, windowSeconds)) {
                return new WindowCounter(now);
            }
            return existing;
        });

        return counter.incrementAndCheck(limit);
    }

    public int getRemainingRequests(String clientId) {
        WindowCounter counter = counters.get(clientId);
        if (counter == null) {
            return limit;
        }

        long now = getCurrentTimeMillis();
        if (counter.isExpired(now, windowSeconds)) {
            return limit;
        }

        return Math.max(0, limit - counter.getCount());
    }

    public long getResetTimeSeconds(String clientId) {
        WindowCounter counter = counters.get(clientId);
        if (counter == null) {
            return windowSeconds;
        }

        long now = getCurrentTimeMillis();
        long windowEnd = counter.getWindowStart() + (windowSeconds * 1000L);
        return Math.max(0, (windowEnd - now) / 1000);
    }

    protected void cleanup() {
        long now = getCurrentTimeMillis();
        counters.entrySet().removeIf(entry ->
            entry.getValue().isExpired(now, windowSeconds * 2) // Keep for 2x window for grace period
        );
    }

    protected long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }

    protected int getActiveClientCount() {
        return counters.size();
    }

    public void shutdown() {
        cleanupExecutor.shutdown();
        try {
            if (!cleanupExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                cleanupExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            cleanupExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private static class WindowCounter {
        private final long windowStart;
        private final AtomicInteger count;

        WindowCounter(long windowStart) {
            this.windowStart = windowStart;
            this.count = new AtomicInteger(0);
        }

        boolean incrementAndCheck(int limit) {
            return count.incrementAndGet() <= limit;
        }

        int getCount() {
            return count.get();
        }

        long getWindowStart() {
            return windowStart;
        }

        boolean isExpired(long now, int windowSeconds) {
            return (now - windowStart) > (windowSeconds * 1000L);
        }
    }
}
