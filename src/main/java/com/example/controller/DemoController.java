package com.example.controller;

import com.example.dto.BookingRequest;
import com.example.dto.UserDTO;
import com.example.mapper.UserMapper;
import com.example.model.BaseParkingSlot;
import com.example.model.Reservation;
import com.example.model.User;
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

    @PostMapping("/error-no-transaction")
    public String demoErrorNoTransaction(@RequestBody UserDTO userDTO) {
        try {
            User user = userMapper.toEntity(userDTO);
            demoService.failedTransactionDemo(user);
            return "Успех";
        } catch (Exception e) {
            return "ОШИБКА (Без транзакции): " + e.getMessage()
                + ". Проверьте pgAdmin — пользователь сохранился";
        }
    }

    @PostMapping("/success-transaction")
    public String demoSuccessTransaction(@RequestBody UserDTO userDTO) {
        try {
            User user = userMapper.toEntity(userDTO);
            demoService.successTransactionDemo(user);
            return "УСПЕХ: И пользователь, и парковка в базе";
        } catch (Exception e) {
            return "ОШИБКА: " + e.getMessage();
        }
    }

    @GetMapping("/n-plus-one")
    public String demonstrateNPlusOne() {
        demoService.demonstrateNPlusOneProblem();
        return "Проблема N+1 выведена в консоль";
    }

    @GetMapping("/solution")
    public String demonstrateSolution() {
        demoService.demonstrateSolution();
        return "Решение N+1 выведено в консоль";
    }

    @PostMapping("/book")
    public ResponseEntity<?> bookSlot(@RequestBody BookingRequest request) {
        try {
            Reservation reservation = demoService.bookSlot(request);
            return new ResponseEntity<>(reservation, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
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