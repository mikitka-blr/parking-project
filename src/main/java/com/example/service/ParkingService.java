package com.example.service;

import com.example.dto.ParkingSlotDTO;
import com.example.mapper.ParkingMapper;
import com.example.model.BaseParkingSlot;
import com.example.repository.ParkingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParkingService {

    private final ParkingRepository repository;
    private final ParkingMapper mapper;

    public ParkingService(ParkingRepository repository, ParkingMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<ParkingSlotDTO> getSlotsByStatus(Boolean occupied) {
        return repository.findAll().stream()
            .filter(slot -> occupied == null || slot.isOccupied() == occupied)
            .map(mapper::toDTO)
            .toList();
    }

    public ParkingSlotDTO getSlotById(Long id) {
        BaseParkingSlot slot = repository.findById(id);
        if (slot == null) {
            return null;
        }
        return mapper.toDTO(slot);
    }

    public List<ParkingSlotDTO> getSlotsByType(String type) {
        return repository.findAll().stream()
            .filter(slot -> slot.getSlotType().equalsIgnoreCase(type))
            .map(mapper::toDTO)
            .toList();
    }
}