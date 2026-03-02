package com.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "regular_slots")
public class RegularParkingSlot extends BaseParkingSlot {
    private boolean isCovered;

    protected RegularParkingSlot() {
    }

    public RegularParkingSlot(String number, boolean occupied, boolean isCovered) {
        super(number, occupied);
        this.isCovered = isCovered;
    }

    @Override
    public String getSlotType() {
        return "REGULAR";
    }

    public boolean isCovered() {
        return isCovered;
    }

    public void setCovered(boolean covered) {
        isCovered = covered;
    }
}