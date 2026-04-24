package com.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CounterServiceTest {

    private CounterService counterService;

    @BeforeEach
    void setUp() {
        counterService = new CounterService();
    }

    @Test
    void testIncrementUnsafe() {
        counterService.incrementUnsafe();
        assertEquals(1, counterService.getUnsafeCounter());
    }

    @Test
    void testIncrementSafe() {
        counterService.incrementSafe();
        assertEquals(1, counterService.getSafeCounter());
    }

    @Test
    void testReset() {
        counterService.incrementUnsafe();
        counterService.incrementSafe();
        
        assertEquals(1, counterService.getUnsafeCounter());
        assertEquals(1, counterService.getSafeCounter());
        
        counterService.reset();
        
        assertEquals(0, counterService.getUnsafeCounter());
        assertEquals(0, counterService.getSafeCounter());
    }

    @Test
    void testIncrementUnsafe_WithInterruption() {
        Thread.currentThread().interrupt();
        
        counterService.incrementUnsafe();
        
        assertTrue(Thread.interrupted());
        assertEquals(1, counterService.getUnsafeCounter());
    }
}
