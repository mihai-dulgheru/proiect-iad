package org.apache.camel.cache;

import java.util.HashMap;
import java.util.Map;

public class SimpleCache {
    private static final Map<String, Object> cache = new HashMap<>();

    public static void put(String key, Object data) {
        cache.put(key, data);
    }

    public static Object get(String key) {
        return cache.get(key);
    }
}
