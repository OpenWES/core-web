package com.openwes.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author xuanloc0511@gmail.com
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimiterStrategy {

    String type() default com.openwes.web.ratelimit.RateLimiter.NONE;

    int maxRequest() default 0;

    long duration() default 1000;
}
