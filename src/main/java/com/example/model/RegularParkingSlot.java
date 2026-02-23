package com.example.model;

public class RegularParkingSlot extends BaseParkingSlot {
    private final boolean isCovered;

    public RegularParkingSlot(Long id, String number, boolean occupied, boolean isCovered) {
        super(id, number, occupied);
        this.isCovered = isCovered;
    }

    @Override
    public String getSlotType() {
        return "REGULAR";
    }

    public boolean isCovered() {
        return isCovered;
    }
}