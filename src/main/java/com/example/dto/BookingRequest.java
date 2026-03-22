package com.example.dto;

import java.time.LocalDateTime;
import java.util.List;

public class BookingRequest {
    private Long userId;
    private Long slotId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<Long> serviceIds;

    /**
     * Пустой конструктор для десериализации JSON.
     */
    public BookingRequest() {
        // Конструктор без параметров
    }

    public BookingRequest(Long userId, Long slotId, LocalDateTime startTime,
                          LocalDateTime endTime, List<Long> serviceIds) {
        this.userId = userId;
        this.slotId = slotId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.serviceIds = serviceIds;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getSlotId() {
        return slotId;
    }

    public void setSlotId(Long slotId) {
        this.slotId = slotId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public List<Long> getServiceIds() {
        return serviceIds;
    }

    public void setServiceIds(List<Long> serviceIds) {
        this.serviceIds = serviceIds;
    }
}