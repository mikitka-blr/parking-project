package com.example.controller;

import com.example.dto.ParkingSlotDTO;
import com.example.model.Reservation;
import com.example.model.User;
import com.example.service.ParkingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @GetMapping
    public List<ParkingSlotDTO> getAll(@RequestParam(required = false) Boolean occupied) {
        return service.getSlotsByStatus(occupied);
    }

    // Бронирование места: /api/slots/1/book
    @PostMapping("/{id}/book")
    public Reservation book(@PathVariable Long id, @RequestBody User user) {
        return service.bookSlot(id, user);
    }
}