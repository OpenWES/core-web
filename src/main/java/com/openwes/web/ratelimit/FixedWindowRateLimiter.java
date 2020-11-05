package com.openwes.web.ratelimit;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author xuanloc0511@gmail.com
 *
 */
public class FixedWindowRateLimiter extends RequestCounter implements RateLimiter {

    private final int maxRequest;
    private final long windowSize;
    private long interval = 0;

    public FixedWindowRateLimiter(int maxRequest, long windowSize) {
        this.maxRequest = maxRequest;
        this.windowSize = windowSize;
    }

    @Override
    public void handle(RoutingContext ctx) {
        String endpoint = new StringBuilder()
                .append(ctx.request().method())
                .append(":")
                .append(ctx.request().path())
                .toString();
        AtomicInteger counter = counterOfEndpoint(endpoint);
        if (counter.compareAndSet(maxRequest, maxRequest)) {
            ctx.fail(HttpResponseStatus.TOO_MANY_REQUESTS.code(), new RuntimeException("Number the request exceeds rate-limit configuration"));
            return;
        }
        counter.addAndGet(1);
        ctx.next();
    }

    @Override
    public void onStart(Vertx vertx) {
        interval = vertx.setPeriodic(windowSize, (event) -> {
            allCounters().forEach((endpoint, counter) -> {
                counter.set(0);
            });
        });
    }

    @Override
    public void onStop(Vertx vertx) {
        vertx.cancelTimer(interval);
    }

}
