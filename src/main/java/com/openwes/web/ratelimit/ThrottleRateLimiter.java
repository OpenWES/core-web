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
public class ThrottleRateLimiter implements RateLimiter {

    private final int maxRequest;
    private final long duration;
    private final AtomicInteger counter = new AtomicInteger(0);
    private final AtomicInteger currentToken = new AtomicInteger(0);
    private long interval = 0;

    public ThrottleRateLimiter(int maxRequest, long duration) {
        this.maxRequest = maxRequest;
        this.duration = duration;
    }

    @Override
    public void handle(RoutingContext ctx) {
        final int i = currentToken.get();
        if (counter.compareAndSet(maxRequest, maxRequest)) {
            ctx.fail(HttpResponseStatus.TOO_MANY_REQUESTS.code(), new RuntimeException("Number the request exceeds rate-limit configuration"));
            return;
        }
        counter.addAndGet(1);
        ctx.addBodyEndHandler((event) -> {
            if (currentToken.compareAndSet(i, i)) {
                counter.addAndGet(-1);
            }
        });
        ctx.next();
    }

    @Override
    public void onStart(Vertx vertx) {
        interval = vertx.setPeriodic(duration, (event) -> {
            counter.set(0);
            currentToken.addAndGet(1);
        });
    }

    @Override
    public void onStop(Vertx vertx) {
        vertx.cancelTimer(interval);
    }

}
