package com.example.model;

public class ElectricParkingSlot extends BaseParkingSlot {
    private final int chargerPower;

    public ElectricParkingSlot(Long id, String number, boolean occupied, int chargerPower) {
        super(id, number, occupied);
        this.chargerPower = chargerPower;
    }

    @Override
    public String getSlotType() {
        return "ELECTRIC";
    }

    public int getChargerPower() {
        return chargerPower;
    }
}