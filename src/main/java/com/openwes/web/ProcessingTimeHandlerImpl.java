/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openwes.web;

import com.openwes.core.utils.ClockService;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author locngo {@literal (locngo@gmail.com)}
 */
public class ProcessingTimeHandlerImpl implements ProcessingTimeHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(ProcessingTimeHandlerImpl.class);

    @Override
    public void handle(RoutingContext ctx) {
        ctx.addBodyEndHandler(v -> {
            String messageId = ctx.request().getHeader("Message-Id");
            long started = Long.valueOf(ctx.response().headers().get(ProcessingTimeHandler.KEY_STARTED));
            long endTime = ClockService.nowMS();
            ctx.response().headers().add(ProcessingTimeHandler.KEY_END, endTime + "");
            long timeElapsed = endTime - started;
            LOGGER.info("Request to {} with id {} take {} ms", ctx.request().absoluteURI(), messageId, timeElapsed);
            ctx.response().headers().add(ProcessingTimeHandler.KEY_ELAPSED, timeElapsed + "");
        });
        ctx.next();
    }

}
