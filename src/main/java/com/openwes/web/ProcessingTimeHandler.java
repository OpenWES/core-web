/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openwes.web;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 *
 * @author locngo {@literal (locngo@gmail.com)}
 */
public interface ProcessingTimeHandler extends Handler<RoutingContext> {

    public final static String KEY_STARTED = "Time-Started",
            KEY_END = "Time-End",
            KEY_ELAPSED = "Time-Elapsed";

    static ProcessingTimeHandler create() {
        return new ProcessingTimeHandlerImpl();
    }
}
