package com.example.controller;

import com.example.dto.ParkingSlotDTO;
import com.example.service.ParkingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/slots")
public class ParkingController {

    private final ParkingService service;

    public ParkingController(ParkingService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ParkingSlotDTO getById(@PathVariable Long id) {
        return service.getSlotById(id);
    }

    @GetMapping
    public List<ParkingSlotDTO> getAll(@RequestParam(required = false) Boolean occupied) {
        return service.getSlotsByStatus(occupied);
    }

    @GetMapping("/type/{type}")
    public List<ParkingSlotDTO> getByType(@PathVariable String type) {
        return service.getSlotsByType(type);
    }
}