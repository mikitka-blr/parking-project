package com.example.dto;

public class ParkingSlotDTO {
    private final Long id;
    private final String number;
    private final String status;
    private final String slotType;
    private final String additionalInfo;

    public ParkingSlotDTO(Long id, String number, String status, String slotType,
                          String additionalInfo) {
        this.id = id;
        this.number = number;
        this.status = status;
        this.slotType = slotType;
        this.additionalInfo = additionalInfo;
    }

    public Long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public String getStatus() {
        return status;
    }

    public String getSlotType() {
        return slotType;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }
}