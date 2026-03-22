package com.example.cache;

import java.time.LocalDateTime;
import java.util.Objects;

public class CacheKey {
    private final String name;
    private final LocalDateTime startDate;
    private final int page;
    private final int size;

    public CacheKey(String name, LocalDateTime startDate, int page, int size) {
        this.name = name;
        this.startDate = startDate;
        this.page = page;
        this.size = size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CacheKey cacheKey = (CacheKey) o;
        return page == cacheKey.page
            && size == cacheKey.size
            && Objects.equals(name, cacheKey.name)
            && Objects.equals(startDate, cacheKey.startDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, startDate, page, size);
    }

    @Override
    public String toString() {
        return "CacheKey{"
            + "name='" + name + '\''
            + ", startDate=" + startDate
            + ", page=" + page
            + ", size=" + size
            + '}';
    }
}