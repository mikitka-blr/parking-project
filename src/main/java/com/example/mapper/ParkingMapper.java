package com.example.mapper;

import com.example.dto.ParkingSlotDTO;
import com.example.model.BaseParkingSlot;
import com.example.model.DisabledParkingSlot;
import com.example.model.ElectricParkingSlot;
import com.example.model.RegularParkingSlot;
import org.springframework.stereotype.Component;

@Component
public class ParkingMapper {

    public ParkingSlotDTO toDTO(BaseParkingSlot slot) {
        String statusText = slot.isOccupied() ? "Occupied" : "Available";
        String additionalInfo = "";


        if (slot instanceof RegularParkingSlot regular) {
            additionalInfo = regular.isCovered() ? "Covered" : "Open air";
        } else if (slot instanceof ElectricParkingSlot electric) {
            additionalInfo = "Charger: " + electric.getChargerPower() + "kW";
        } else if (slot instanceof DisabledParkingSlot disabled) {
            additionalInfo = disabled.hasWiderEntrance() ? "Wider entrance" : "Standard entrance";
        }

        return new ParkingSlotDTO(
            slot.getId(),
            slot.getNumber(),
            statusText,
            slot.getSlotType(),
            additionalInfo
        );
    }
}