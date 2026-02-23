package com.example.model;

public class DisabledParkingSlot extends BaseParkingSlot {
    private final boolean hasWiderEntrance;

    public DisabledParkingSlot(Long id, String number, boolean occupied, boolean hasWiderEntrance) {
        super(id, number, occupied);
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