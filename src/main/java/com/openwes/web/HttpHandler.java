package com.openwes.web;

import com.openwes.core.utils.Utils;
import com.openwes.core.utils.Validate;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerResponse;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author xuanloc0511@gmail.com
 * 
 */
public abstract class HttpHandler extends HttpParams {

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpHandler.class);
    private HttpServerResponse response;
    private String requestPath;
    private MultiMap queryParams;
    private MultiMap headers;
    private Map<String, String> pathParams;
    private String requestId;
    private String rawBodyRequest;

    HttpHandler setRequestPath(String requestPath) {
        this.requestPath = requestPath;
        return this;
    }

    HttpHandler setResponse(HttpServerResponse response) {
        this.response = response;
        return this;
    }

    HttpHandler setQueryParams(MultiMap queryParams) {
        this.queryParams = queryParams;
        return this;
    }

    HttpHandler setHeaders(MultiMap headers) {
        this.headers = headers;
        return this;
    }

    HttpHandler setPathParams(Map<String, String> pathParams) {
        this.pathParams = pathParams;
        return this;
    }

    HttpHandler setRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    HttpHandler setRawBodyRequest(String rawBodyRequest) {
        this.rawBodyRequest = rawBodyRequest;
        return this;
    }

    protected final String bodyAsString() {
        return rawBodyRequest;
    }

    protected final <T> T bodyAsObject(Class<T> clzz) {
        return Validate.isEmpty(rawBodyRequest)
                ? null : Utils.unmarshal(rawBodyRequest, clzz);
    }

    protected final String requestId() {
        return requestId;
    }

    protected final MultiMap queryParams() {
        return queryParams;
    }

    protected final MultiMap headers() {
        return headers;
    }

    protected final Map<String, String> pathParams() {
        return pathParams;
    }

    protected final void response(ResponseMessage obj) {
        if (response == null) {
            return;
        }
        if (response.ended() || response.closed()) {
            return;
        }
        try {
            if (obj == null) {
                response.end(ResponseMessage.success(HttpResponseStatus.OK.code(), "OK", null).json());
                return;
            }
            String payload = obj.json();
            LOGGER.info("Response of request to {} with id {} is {}", requestPath, requestId, payload);
            response.end(payload);
        } catch (Exception ex) {
            LOGGER.error("Exception while response to client", ex);
            response.end(ResponseMessage.error(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), ex.getMessage()).json());
        } finally {
        }
    }

    public abstract void handle() throws Exception;
}
