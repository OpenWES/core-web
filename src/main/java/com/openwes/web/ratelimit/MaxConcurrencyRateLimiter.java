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
public class MaxConcurrencyRateLimiter extends RequestCounter implements RateLimiter {

    private final int maxConcurrent;

    public MaxConcurrencyRateLimiter(int maxConcurrent) {
        this.maxConcurrent = maxConcurrent;
    }

    @Override
    public void handle(RoutingContext ctx) {
        String endpoint = new StringBuilder()
                .append(ctx.request().method())
                .append(":")
                .append(ctx.request().path())
                .toString();
        AtomicInteger counter = counterOfEndpoint(endpoint);
        int max = maxRequestOf(endpoint, maxConcurrent);
        if (counter.compareAndSet(max, max)) {
            ctx.fail(HttpResponseStatus.TOO_MANY_REQUESTS.code(), new RuntimeException("Number the request exceeds rate-limit configuration"));
            return;
        }
        counter.getAndIncrement();
        ctx.addBodyEndHandler((event) -> {
            counter.getAndDecrement();
        });
        ctx.next();
    }

    @Override
    public void onStart(Vertx vertx) {
    }

    @Override
    public void onStop(Vertx vertx) {
    }

}
