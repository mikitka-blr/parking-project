package com.example.model;

import jakarta.persistence.Entity;

@Entity
public class ElectricParkingSlot extends BaseParkingSlot {
    private int chargerPower;

    protected ElectricParkingSlot() {
    }

    public ElectricParkingSlot(String number, boolean occupied, int chargerPower) {
        super(number, occupied);
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