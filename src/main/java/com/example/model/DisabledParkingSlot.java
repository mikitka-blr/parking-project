package com.example.model;

import jakarta.persistence.Entity;

@Entity
public class DisabledParkingSlot extends BaseParkingSlot {
    private boolean hasWiderEntrance;

    protected DisabledParkingSlot() {
    }

    public DisabledParkingSlot(String number, boolean occupied, boolean hasWiderEntrance) {
        super(number, occupied);
        this.hasWiderEntrance = hasWiderEntrance;
    }

    @Override
    public String getSlotType() {
        return "DISABLED";
    }

    public boolean hasWiderEntrance() {
        return hasWiderEntrance;
    }
}