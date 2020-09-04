package com.openwes.web;

import com.openwes.core.utils.ClassLoadException;
import com.openwes.core.utils.ClassUtils;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 *
 * @author Deadpool {@literal (locngo@fortna.com)}
 * @since Jun 24, 2019
 * @version 1.0.0
 *
 */
class ApiHandler implements Handler<RoutingContext> {

    private final static Logger LOGGER = LoggerFactory.getLogger(ApiHandler.class);
    private final String path;
    private final String httpHandler;

    private ApiHandler(String path, String httpHandler) {
        this.path = path;
        this.httpHandler = httpHandler;
    }

    public final static ApiHandler create(String path, String httpHandler) {
        return new ApiHandler(path, httpHandler);
    }

    @Override
    public void handle(RoutingContext ctx) {
        ctx.request().connection()
                .exceptionHandler((Throwable e) -> {
                    LOGGER.error("Connection of request {} get error ", path, e);
                });

        HttpHandler handler = null;
        try {
            handler = ClassUtils.object(httpHandler);
        } catch (ClassLoadException ex) {
            LOGGER.error("init http handler get error", ex);
            ctx.response().end(ResponseMessage.error(
                    HttpResponseStatus.INTERNAL_SERVER_ERROR.code(),
                    "Can not init handler for API " + path)
                    .json());
            return;
        }

        String messageId = ctx.request().getHeader("Message-ID");
        try {
            MDC.put("txid", messageId);
            handler.setRequestPath(path)
                    .setRequestId(messageId)
                    .setResponse(ctx.response())
                    .setHeaders(ctx.request().headers())
                    .setPathParams(ctx.pathParams())
                    .setQueryParams(ctx.queryParams())
                    .setRawBodyRequest(ctx.getBodyAsString())
                    .handle();
        } catch (Exception ex) {
            LOGGER.error("Process request to {} with id {} get error", path, messageId, ex);
            ctx.response().end(ResponseMessage.error(
                    HttpResponseStatus.INTERNAL_SERVER_ERROR.code(),
                    ex != null ? ex.getMessage() : "Internal server error")
                    .json());
        }
    }

}
