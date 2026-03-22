package com.example.service;

import com.example.cache.CacheKey;
import com.example.model.Reservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReservationCacheService {

    private static final Logger LOG = LoggerFactory.getLogger(ReservationCacheService.class);
    private final Map<CacheKey, List<Reservation>> cache = new HashMap<>();

    public List<Reservation> getFromCache(String name, LocalDateTime startDate, int page, int size) {
        CacheKey key = new CacheKey(name, startDate, page, size);
        return cache.get(key);
    }

    public Page<Reservation> getPageFromCache(String name, LocalDateTime startDate, int page, int size) {
        List<Reservation> list = getFromCache(name, startDate, page, size);
        if (list != null) {
            LOG.debug("Данные получены из кэша");
            return new PageImpl<>(list);
        }
        return null;
    }

    public void putInCache(String name, LocalDateTime startDate, int page, int size, List<Reservation> reservations) {
        CacheKey key = new CacheKey(name, startDate, page, size);
        cache.put(key, reservations);
        LOG.debug("Добавлено в кэш. Размер: {}", reservations.size());
    }

    public void clearCache() {
        int size = cache.size();
        cache.clear();
        LOG.info("Кэш очищен. Размер: {}", size);
    }

    public int cacheSize() {
        return cache.size();
    }

    public boolean containsKey(String name, LocalDateTime startDate, int page, int size) {
        CacheKey key = new CacheKey(name, startDate, page, size);
        return cache.containsKey(key);
    }
}