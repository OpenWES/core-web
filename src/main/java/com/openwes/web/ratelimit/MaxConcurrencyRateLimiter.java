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
public class MaxConcurrencyRateLimiter implements RateLimiter {

    private final int maxConcurrent;
    private final AtomicInteger counter = new AtomicInteger(0);

    public MaxConcurrencyRateLimiter(int maxConcurrent) {
        this.maxConcurrent = maxConcurrent;
    }

    @Override
    public void handle(RoutingContext ctx) {
        if (counter.compareAndSet(maxConcurrent, maxConcurrent)) {
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
