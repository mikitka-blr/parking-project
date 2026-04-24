package com.example.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class CounterService {
    private int unsafeCounter = 0;
    private final AtomicInteger safeCounter = new AtomicInteger(0);

    public void incrementUnsafe() {
        int temp = unsafeCounter;
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        unsafeCounter = temp + 1;
    }

    public void incrementSafe() {
        safeCounter.incrementAndGet();
    }

    public int getUnsafeCounter() {
        return unsafeCounter;
    }

    public int getSafeCounter() {
        return safeCounter.get();
    }

    public void reset() {
        unsafeCounter = 0;
        safeCounter.set(0);
    }
}

