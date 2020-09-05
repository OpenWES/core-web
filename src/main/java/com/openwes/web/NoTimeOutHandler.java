package com.openwes.web;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * 
 * @author xuanloc0511@gmail.com
 * 
 */
public class NoTimeOutHandler implements Handler<RoutingContext>{

    @Override
    public void handle(RoutingContext ctx) {
        ctx.next();
    }

}
