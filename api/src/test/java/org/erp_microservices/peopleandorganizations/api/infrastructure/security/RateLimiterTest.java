package org.erp_microservices.peopleandorganizations.api.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimiterTest {

    private RateLimiter rateLimiter;
    private Clock fixedClock;

    @BeforeEach
    void setUp() {
        rateLimiter = new RateLimiter(5, 60); // 5 requests per minute
        fixedClock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
    }

    @Test
    void allowRequest_shouldAllowFirstRequestsUpToLimit() {
        String clientId = "192.168.1.1";

        for (int i = 0; i < 5; i++) {
            assertThat(rateLimiter.allowRequest(clientId))
                .as("Request %d should be allowed", i + 1)
                .isTrue();
        }

        assertThat(rateLimiter.allowRequest(clientId))
            .as("6th request should be denied")
            .isFalse();
    }

    @Test
    void allowRequest_shouldTrackDifferentClientsIndependently() {
        String client1 = "192.168.1.1";
        String client2 = "192.168.1.2";

        // Exhaust client1's limit
        for (int i = 0; i < 5; i++) {
            assertThat(rateLimiter.allowRequest(client1)).isTrue();
        }
        assertThat(rateLimiter.allowRequest(client1)).isFalse();

        // Client2 should still be allowed
        for (int i = 0; i < 5; i++) {
            assertThat(rateLimiter.allowRequest(client2))
                .as("Client2 request %d should be allowed", i + 1)
                .isTrue();
        }
        assertThat(rateLimiter.allowRequest(client2)).isFalse();
    }

    @Test
    void allowRequest_shouldResetAfterTimeWindow() {
        // Create a rate limiter with a testable clock
        TestableRateLimiter testableRateLimiter = new TestableRateLimiter(5, 1); // 1 second window
        String clientId = "192.168.1.1";

        // Exhaust the limit
        for (int i = 0; i < 5; i++) {
            assertThat(testableRateLimiter.allowRequest(clientId)).isTrue();
        }
        assertThat(testableRateLimiter.allowRequest(clientId)).isFalse();

        // Advance time by 1.1 seconds
        testableRateLimiter.advanceTimeSeconds(2);

        // Should be allowed again
        assertThat(testableRateLimiter.allowRequest(clientId))
            .as("Request should be allowed after time window reset")
            .isTrue();
    }

    @Test
    void allowRequest_shouldBeThreadSafe() throws InterruptedException {
        String clientId = "192.168.1.1";
        int threadCount = 10;
        int requestsPerThread = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger allowedRequests = new AtomicInteger(0);
        AtomicInteger deniedRequests = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < requestsPerThread; j++) {
                        if (rateLimiter.allowRequest(clientId)) {
                            allowedRequests.incrementAndGet();
                        } else {
                            deniedRequests.incrementAndGet();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // Total requests = 10 threads * 2 requests = 20
        // With limit of 5, we should have exactly 5 allowed and 15 denied
        assertThat(allowedRequests.get()).isEqualTo(5);
        assertThat(deniedRequests.get()).isEqualTo(15);
    }

    @Test
    void getRemainingRequests_shouldReturnCorrectCount() {
        String clientId = "192.168.1.1";

        assertThat(rateLimiter.getRemainingRequests(clientId)).isEqualTo(5);

        rateLimiter.allowRequest(clientId);
        assertThat(rateLimiter.getRemainingRequests(clientId)).isEqualTo(4);

        rateLimiter.allowRequest(clientId);
        assertThat(rateLimiter.getRemainingRequests(clientId)).isEqualTo(3);

        // Exhaust remaining
        for (int i = 0; i < 3; i++) {
            rateLimiter.allowRequest(clientId);
        }
        assertThat(rateLimiter.getRemainingRequests(clientId)).isEqualTo(0);
    }

    @Test
    void getResetTimeSeconds_shouldReturnTimeUntilReset() {
        TestableRateLimiter testableRateLimiter = new TestableRateLimiter(5, 60);
        String clientId = "192.168.1.1";

        // Make a request to initialize the window
        testableRateLimiter.allowRequest(clientId);

        // Should be approximately 60 seconds
        long resetTime = testableRateLimiter.getResetTimeSeconds(clientId);
        assertThat(resetTime).isBetween(58L, 60L);

        // Advance time
        testableRateLimiter.advanceTimeSeconds(30);
        resetTime = testableRateLimiter.getResetTimeSeconds(clientId);
        assertThat(resetTime).isBetween(28L, 30L);
    }

    @Test
    void cleanup_shouldRemoveExpiredEntries() {
        TestableRateLimiter testableRateLimiter = new TestableRateLimiter(5, 60);

        // Create entries for multiple clients
        testableRateLimiter.allowRequest("client1");
        testableRateLimiter.allowRequest("client2");
        testableRateLimiter.allowRequest("client3");

        assertThat(testableRateLimiter.getActiveClients()).isEqualTo(3);

        // Advance time past expiration (2x window for grace period)
        testableRateLimiter.advanceTimeSeconds(240); // 2 * 120 seconds

        // Trigger cleanup
        testableRateLimiter.cleanup();

        assertThat(testableRateLimiter.getActiveClients()).isEqualTo(0);
    }

    @Test
    void constructor_shouldValidateParameters() {
        // Valid parameters
        RateLimiter valid = new RateLimiter(10, 60);
        assertThat(valid).isNotNull();

        // Invalid limit
        try {
            new RateLimiter(0, 60);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("Limit must be positive");
        }

        // Invalid window
        try {
            new RateLimiter(10, 0);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("Window must be positive");
        }
    }

    // Test helper class with controllable time
    private static class TestableRateLimiter extends RateLimiter {
        private long currentTimeMillis;

        public TestableRateLimiter(int limit, int windowSeconds) {
            super(limit, windowSeconds);
            this.currentTimeMillis = System.currentTimeMillis();
        }

        @Override
        protected long getCurrentTimeMillis() {
            return currentTimeMillis;
        }

        public void advanceTimeSeconds(int seconds) {
            currentTimeMillis += seconds * 1000L;
        }

        public int getActiveClients() {
            return super.getActiveClientCount();
        }
    }
}
