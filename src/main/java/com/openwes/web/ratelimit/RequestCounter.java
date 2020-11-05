package com.openwes.web.ratelimit;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author xuanloc0511@gmail.com
 */
public class RequestCounter {

    private final Map<String, AtomicInteger> counter = new HashMap<>();
    private final AtomicInteger zeroCounter = new AtomicInteger(0);

    public void registerEndpoint(String endpoint) {
        counter.put(endpoint, new AtomicInteger(0));
    }

    public Map<String, AtomicInteger> allCounters() {
        return counter;
    }

    public AtomicInteger counterOfEndpoint(String endpoint) {
        AtomicInteger c = counter.get(endpoint);
        if (c != null) {
            return c;
        }
        return zeroCounter;
    }

    public int countRequest(String endpoint) {
        AtomicInteger c = counter.get(endpoint);
        if (c == null) {
            return 0;
        }
        return c.incrementAndGet();
    }

    public int countResponse(String endpoint) {
        AtomicInteger c = counter.get(endpoint);
        if (c == null) {
            return 0;
        }
        return c.decrementAndGet();
    }
}
