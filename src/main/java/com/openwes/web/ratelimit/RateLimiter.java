package com.openwes.web.ratelimit;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

/**
 *
 * @author xuanloc0511@gmail.com
 *
 */
public interface RateLimiter extends Handler<RoutingContext> {

    public final static String NONE = "NONE",
            FIXED = "FIXED_WINDOW",
            MAXCONCUR = "MAX_CONCURRENCY",
            THROTTLE = "THROTTLE";

    public static RateLimiter create(String type, int maxRequest, long duration) {
        if (type == null || maxRequest == 0) {
            return new NoRateLimiter();
        }
        if (NONE.equals(type)) {
            return new NoRateLimiter();
        }
        if (THROTTLE.equals(type)) {
            return new ThrottleRateLimiter(maxRequest, duration);
        }
        if (MAXCONCUR.equals(type)) {
            return new MaxConcurrencyRateLimiter(maxRequest);
        }
        if (FIXED.equals(type)) {
            return new FixedWindowRateLimiter(maxRequest, duration);
        }
        return new NoRateLimiter();
    }

    public void onStart(Vertx vertx);

    public void onStop(Vertx vertx);
}
