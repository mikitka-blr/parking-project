package com.example.service;

import com.example.model.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReservationCacheServiceTest {

    private ReservationCacheService cacheService;
    private final String testName = "Test";
    private final LocalDateTime testDate = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        cacheService = new ReservationCacheService();
    }

    @Test
    void testPutAndGetFromCache() {
        List<Reservation> resList = List.of(new Reservation());
        
        cacheService.putInCache(testName, testDate, 0, 10, resList);
        List<Reservation> cached = cacheService.getFromCache(testName, testDate, 0, 10);
        
        assertNotNull(cached);
        assertEquals(1, cached.size());
    }

    @Test
    void testGetPageFromCache_Found() {
        List<Reservation> resList = List.of(new Reservation());
        cacheService.putInCache(testName, testDate, 0, 10, resList);
        
        Page<Reservation> page = cacheService.getPageFromCache(testName, testDate, 0, 10);
        
        assertNotNull(page);
        assertEquals(1, page.getContent().size());
    }

    @Test
    void testGetPageFromCache_NotFound() {
        Page<Reservation> page = cacheService.getPageFromCache("WrongName", testDate, 0, 10);
        assertNull(page);
    }

    @Test
    void testClearCache() {
        cacheService.putInCache(testName, testDate, 0, 10, List.of(new Reservation()));
        assertEquals(1, cacheService.cacheSize());
        
        cacheService.clearCache();
        
        assertEquals(0, cacheService.cacheSize());
    }

    @Test
    void testContainsKey() {
        cacheService.putInCache(testName, testDate, 0, 10, List.of());
        
        assertTrue(cacheService.containsKey(testName, testDate, 0, 10));
        assertFalse(cacheService.containsKey("Other", testDate, 0, 10));
    }
}

