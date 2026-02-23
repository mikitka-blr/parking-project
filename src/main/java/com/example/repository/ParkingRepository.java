package com.example.repository;

import com.example.model.BaseParkingSlot;
import com.example.model.DisabledParkingSlot;
import com.example.model.ElectricParkingSlot;
import com.example.model.RegularParkingSlot;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ParkingRepository {
    private final List<BaseParkingSlot> slots = new ArrayList<>();

    public ParkingRepository() {
        slots.add(new RegularParkingSlot(1L, "A-101", false, true));
        slots.add(new ElectricParkingSlot(2L, "B-202", true, 50));
        slots.add(new DisabledParkingSlot(3L, "C-303", false, true));
        slots.add(new RegularParkingSlot(4L, "D-404", true, false));
    }

    public List<BaseParkingSlot> findAll() {
        return slots;
    }

    public BaseParkingSlot findById(Long id) {
        return slots.stream()
            .filter(s -> s.getId().equals(id))
            .findFirst()
            .orElse(null);
    }
}