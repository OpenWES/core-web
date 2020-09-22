package com.openwes.web.ratelimit;

import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

/**
 *
 * @author xuanloc0511@gmail.com
 *
 */
public class NoRateLimiter implements RateLimiter {

    @Override
    public void handle(RoutingContext event) {
        event.next();
    }

    @Override
    public void onStart(Vertx vertx) {
    }

    @Override
    public void onStop(Vertx vertx) {
    }

}
