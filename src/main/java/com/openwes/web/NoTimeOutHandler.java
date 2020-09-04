package com.openwes.web;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 *
 * @author Deadpool {@literal (locngo@fortna.com)}
 * @since Apr 7, 2020
 * @version 1.0.0
 *
 */
public class NoTimeOutHandler implements Handler<RoutingContext>{

    @Override
    public void handle(RoutingContext ctx) {
        ctx.next();
    }

}
