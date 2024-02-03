package org.apache.camel;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class UniqueIdentifierGenerator {
    private static final AtomicLong counter = new AtomicLong();
    private static final Random random = new Random();

    public static String generate() {
        long currentCount = counter.incrementAndGet();
        int randomComponent = random.nextInt(10000);
        long timestamp = System.currentTimeMillis();
        return timestamp + "-" + currentCount + "-" + randomComponent;
    }
}
