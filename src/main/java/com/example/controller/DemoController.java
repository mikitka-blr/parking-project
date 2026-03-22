package com.example.controller;

import com.example.dto.BookingRequest;
import com.example.mapper.UserMapper;
import com.example.model.BaseParkingSlot;
import com.example.model.Reservation;
import com.example.service.DemoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/demo")
public class DemoController {

    private final DemoService demoService;
    private final UserMapper userMapper;

    public DemoController(DemoService demoService, UserMapper userMapper) {
        this.demoService = demoService;
        this.userMapper = userMapper;
    }

    @PostMapping("/book")
    public ResponseEntity<Reservation> bookSlot(@RequestBody BookingRequest request) {
        Reservation reservation = demoService.bookSlot(request);
        if (reservation == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(reservation, HttpStatus.CREATED);
    }

    @GetMapping("/users/{userId}/reservations")
    public ResponseEntity<List<Reservation>> getUserReservations(@PathVariable Long userId) {
        List<Reservation> reservations = demoService.getUserReservations(userId);
        if (reservations.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @GetMapping("/slots/available")
    public ResponseEntity<List<BaseParkingSlot>> getAvailableSlots() {
        List<BaseParkingSlot> slots = demoService.getAvailableSlots();
        if (slots.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(slots, HttpStatus.OK);
    }
}