package com.example.service;

import com.example.model.BaseParkingSlot;
import com.example.repository.BaseParkingSlotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParkingSlotService {

    private final BaseParkingSlotRepository slotRepository;

    public ParkingSlotService(BaseParkingSlotRepository slotRepository) {
        this.slotRepository = slotRepository;
    }

    public List<BaseParkingSlot> getAllSlots() {
        return slotRepository.findAll();
    }

    public BaseParkingSlot getSlotById(Long id) {
        return slotRepository.findById(id).orElse(null);
    }

    public List<BaseParkingSlot> getAvailableSlots() {
        return slotRepository.findAll().stream()
            .filter(slot -> !slot.isOccupied())
            .collect(Collectors.toList());
    }

    public List<BaseParkingSlot> getSlotsByType(String type) {
        return slotRepository.findAll().stream()
            .filter(slot -> slot.getSlotType().equalsIgnoreCase(type))
            .collect(Collectors.toList());
    }

    @Transactional
    public BaseParkingSlot occupySlot(Long id) {
        return slotRepository.findById(id)
            .map(slot -> {
                slot.setOccupied(true);
                return slotRepository.save(slot);
            })
            .orElse(null);
    }

    @Transactional
    public BaseParkingSlot freeSlot(Long id) {
        return slotRepository.findById(id)
            .map(slot -> {
                slot.setOccupied(false);
                return slotRepository.save(slot);
            })
            .orElse(null);
    }
}