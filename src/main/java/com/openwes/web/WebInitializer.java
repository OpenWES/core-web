package com.openwes.web;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.openwes.core.Application;
import com.openwes.core.interfaces.Initializer;
import com.openwes.core.utils.ClassUtils;
import com.openwes.core.utils.ClockService;
import com.openwes.core.utils.KeystoreOpts;
import com.openwes.core.utils.UniqId;
import com.openwes.core.utils.Utils;
import com.openwes.core.utils.Validate;
import com.openwes.web.annotation.Api;
import com.typesafe.config.Config;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpConnection;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.JdkSSLEngineOptions;
import io.vertx.core.net.JksOptions;
import io.vertx.core.net.OpenSSLEngineOptions;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.core.net.PfxOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.TimeoutHandler;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author xuanloc0511@gmail.com
 */
public class WebInitializer implements Initializer {

    private final static Logger LOGGER = LoggerFactory.getLogger(WebInitializer.class);

    private Vertx vertx;

    @Override
    public String configKey() {
        return "web";
    }

    @Override
    public void onStart(Config config) throws Exception {
        int evSize = config.getInt("thread-size.event-loop");
        if (evSize <= 1) {
            evSize = 1;
        }
        int workerSize = config.getInt("thread-size.worker");
        if (workerSize <= 1) {
            workerSize = 1;
        }
        workerSize = Math.max(Runtime.getRuntime().availableProcessors(), workerSize);

        String host = config.getString("host");
        if (Validate.isEmpty(host)) {
            throw new RuntimeException("missing host address");
        }

        int port = config.getInt("port");
        if (port < Utils.MIN_PORT || port > Utils.MAX_PORT) {
            throw new RuntimeException("port must be between 1 and 65535");
        }

        vertx = Vertx.vertx(new VertxOptions()
                .setEventLoopPoolSize(evSize)
                .setWorkerPoolSize(workerSize)
                .setMaxEventLoopExecuteTime(300)
                .setMaxEventLoopExecuteTimeUnit(TimeUnit.SECONDS)
                .setMaxWorkerExecuteTime(300)
                .setMaxWorkerExecuteTimeUnit(TimeUnit.SECONDS)
                .setBlockedThreadCheckIntervalUnit(TimeUnit.SECONDS)
                .setBlockedThreadCheckInterval(10))
                .exceptionHandler((Throwable event) -> {
                    LOGGER.error("Handle request get exception ", event);
                });

        Router router = Router.router(vertx);
        router.route()
                .handler(CorsHandler.create("*")
                        .allowedMethod(HttpMethod.GET)
                        .allowedMethod(HttpMethod.POST)
                        .allowedMethod(HttpMethod.PUT)
                        .allowedMethod(HttpMethod.DELETE)
                        .allowedMethod(HttpMethod.OPTIONS)
                        .allowedMethod(HttpMethod.PATCH)
                        .allowedMethod(HttpMethod.HEAD)
                        .allowedMethod(HttpMethod.TRACE)
                        .allowedMethod(HttpMethod.CONNECT)
                        .allowedMethod(HttpMethod.OTHER)
                        .allowedHeader("*"))
                .handler(ctx -> {
                    ctx.response()
                            .setStatusCode(200)
                            .putHeader("Access-Control-Allow-Headers", "*")
                            .putHeader("Access-Control-Allow-Credentials", "true")
                            .putHeader("Content-Type", "application/json; charset=utf-8")
                            .putHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate")
                            .putHeader("Pragma", "no-cache")
                            .putHeader("Expires", "0");
                    ctx.next();
                });

        /**
         * setup restful
         */
        setupApi(config.getStringList("packages"),
                router,
                config.hasPath("process-timeout") ? config.getLong("process-timeout") : 0L);

        HttpServerOptions httpServerOptions = new HttpServerOptions();
        boolean sslEnabled = false;
        if (config.hasPath("ssl-enabled")) {
            sslEnabled = config.getBoolean("ssl-enabled");
            if (sslEnabled) {
                String sslEngine = "default";
                if (config.hasPath("ssl-engine")) {
                    sslEngine = config.getString("ssl-engine");
                }
                setupSSL(httpServerOptions, sslEngine);
            }
        }

        final int _port = port;
        final String _host = host;
        CountDownLatch cdl = new CountDownLatch(0);
        vertx.createHttpServer(httpServerOptions)
                .requestHandler(router)
                .exceptionHandler((Throwable e) -> {
                    LOGGER.error("HttpInstance can not handle this request. ", e);
                })
                .connectionHandler((HttpConnection event) -> {
                    LOGGER.debug("handle connection to http server {}", event);
                })
                .listen(_port, _host, (AsyncResult<HttpServer> e) -> {
                    if (e.succeeded()) {
                        LOGGER.info("Complete starting http protocol successful", e.succeeded());
                        cdl.countDown();
                    } else {
                        LOGGER.error("Start http protocol get exception ", e.cause());
                        cdl.countDown();
                    }
                });
        LOGGER.info("Binding HTTP{} listener at {}...", sslEnabled ? "S" : "", port);
    }

    @Override
    public void onShutdow(Config config) throws Exception {
    }

    private void setupApi(List<String> packages, Router router, long timeoutInMs) throws Exception {
        router.route("/*")
                .handler(BodyHandler.create())
                .handler((RoutingContext ctx) -> {
                    String messageId = UniqId.uniqId16Bytes();
                    ctx.request().headers().set("Message-Id", messageId);
                    if (Validate.isEmpty(ctx.getBodyAsString())) {
                        LOGGER.info("Receive {} request from {} to uri = {} with id = {}",
                                ctx.request().method(),
                                ctx.request().remoteAddress(),
                                ctx.request().absoluteURI(),
                                messageId);
                    } else {
                        LOGGER.info("Receive {} request from {} to uri = {} with id = {} and input = {}",
                                ctx.request().method(),
                                ctx.request().remoteAddress(),
                                ctx.request().absoluteURI(),
                                messageId,
                                ctx.getBodyAsString());
                    }
                    ctx.response().headers().add(ProcessingTimeHandler.KEY_STARTED, "" + ClockService.nowMS());
                    ctx.next();
                })
                .handler(ProcessingTimeHandler.create())
                .handler(timeoutInMs > 0 ? TimeoutHandler.create(timeoutInMs, HttpResponseStatus.GATEWAY_TIMEOUT.code()) : new NoTimeOutHandler())
                .failureHandler((RoutingContext ctx) -> {
                    String messageId = ctx.request().getHeader("Message-Id");
                    if (ctx.statusCode() == -1) {
                        if (ctx.failure() == null) {
                            LOGGER.error("Request to {} with message id {} get unexpected error", ctx.request().absoluteURI());
                            ctx.response()
                                    .setStatusCode(200)
                                    .end(ResponseMessage.error(
                                            HttpResponseStatus.INTERNAL_SERVER_ERROR.code(),
                                            HttpResponseStatus.INTERNAL_SERVER_ERROR.reasonPhrase())
                                            .json());
                        } else {
                            LOGGER.error("Request to {} with message id {} get exception", ctx.request().absoluteURI(), messageId, ctx.failure());
                            ctx.response()
                                    .setStatusCode(200)
                                    .end(ResponseMessage.error(
                                            HttpResponseStatus.INTERNAL_SERVER_ERROR.code(),
                                            ctx.failure().getMessage())
                                            .json());
                        }
                        return;
                    }
                    LOGGER.error("Request to {} with message id {} get error with code {}", ctx.request().absoluteURI(), messageId, ctx.statusCode());
                    String errorMsg = "error";
                    if (ctx.failure() != null) {
                        errorMsg = ctx.failure().getMessage();
                    }
                    ctx.response()
                            .setStatusCode(200)
                            .end(ResponseMessage.error(ctx.statusCode(), errorMsg).json());
                });

        for (String packageName : packages) {
            ImmutableSet<ClassPath.ClassInfo> classInfos = ClassPath.from(Thread.currentThread().getContextClassLoader())
                    .getTopLevelClassesRecursive(packageName);
            for (ClassPath.ClassInfo ci : classInfos) {
                registerApi(router, ci);
            }
        }
    }

    private void registerApi(Router router, ClassPath.ClassInfo ci) throws Exception {
        Class clzz = ClassUtils.load(ci.getName());
        Annotation anno = clzz.getDeclaredAnnotation(Api.class);
        if (anno == null || !(anno instanceof Api)) {
            return;
        }
        HttpMethod method = ((Api) anno).method();
        String path = ((Api) anno).path();
        if (Validate.isEmpty(path)) {
            return;
        }
        router.route(method, path)
                .useNormalisedPath(true)
                .blockingHandler(ApiHandler.create(path, ci.getName()), false);
    }

    private void setupSSL(HttpServerOptions httpOpts, String sslEngine) {
        KeystoreOpts keystoreOpts = Application.keystoreOpts();
        if (keystoreOpts.getKeystoreType().endsWith("jks")
                || keystoreOpts.getKeystoreType().endsWith("keystore")) {
            httpOpts.setKeyStoreOptions(new JksOptions()
                    .setPassword(keystoreOpts.getKeystorePass())
                    .setPath(keystoreOpts.getKeystorePath()))
                    .setSsl(true);
            // Use a PKCS12 keystore
        } else if (keystoreOpts.getKeystoreType().endsWith("pfx")
                || keystoreOpts.getKeystoreType().endsWith("p12")) {
            httpOpts.setPfxKeyCertOptions(new PfxOptions()
                    .setPassword(keystoreOpts.getKeystorePass())
                    .setPath(keystoreOpts.getKeystorePath()))
                    .setSsl(true);
            // Use a PEM key/cert pair
        } else if (keystoreOpts.getKeystoreType().endsWith("pem")) {
            httpOpts.setPemKeyCertOptions(new PemKeyCertOptions()
                    .setCertPath(keystoreOpts.getKeystorePath())
                    .setKeyPath(keystoreOpts.getKeystorePath()));
            httpOpts.setSsl(true);
        } else {
            throw new RuntimeException("Type " + keystoreOpts.getKeystoreType() + " of cert is not supported");
        }
        httpOpts.setUseAlpn(false);
        if (Validate.isEmpty(sslEngine)
                || sslEngine.equals("default")) {
            /**
             * we don't need set anything
             */
        } else if (sslEngine.equals("openSSL")) {
            httpOpts.setOpenSslEngineOptions(new OpenSSLEngineOptions());
        } else if (sslEngine.equals("jdkSSL")) {
            httpOpts.setJdkSslEngineOptions(new JdkSSLEngineOptions());
        }
    }
}
