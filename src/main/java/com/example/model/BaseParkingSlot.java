package com.example.model;

public abstract class BaseParkingSlot {
    private final Long id;
    private final String number;
    private boolean occupied;


    protected BaseParkingSlot(Long id, String number, boolean occupied) {
        this.id = id;
        this.number = number;
        this.occupied = occupied;
    }

    public abstract String getSlotType();

    public Long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }
}