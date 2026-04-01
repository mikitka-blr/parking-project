package com.example.controller;

import com.example.dto.BookingRequest;
import com.example.mapper.UserMapper;
import com.example.model.BaseParkingSlot;
import com.example.model.Reservation;
import com.example.service.DemoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(name = "Бронирование", description = "Методы для бронирования мест и управления кэшем")
public class DemoController {

    private final DemoService demoService;
    private final UserMapper userMapper;

    public DemoController(DemoService demoService, UserMapper userMapper) {
        this.demoService = demoService;
        this.userMapper = userMapper;
    }

    @PostMapping("/book")
    @Operation(
        summary = "Забронировать место",
        description = "Создает новую бронь, помечает место как занятое. "
            + "После создания брони происходит ИНВАЛИДАЦИЯ КЭША (очистка всего in-memory кэша)."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Бронь успешно создана"),
        @ApiResponse(responseCode = "400", description = "Неверные параметры запроса"),
        @ApiResponse(responseCode = "404", description = "Пользователь или место не найдены"),
        @ApiResponse(responseCode = "409", description = "Место уже занято")
    })
    public ResponseEntity<Reservation> bookSlot(@Valid @RequestBody BookingRequest request) {
        Reservation reservation = demoService.bookSlot(request);
        if (reservation == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(reservation, HttpStatus.CREATED);
    }

    @GetMapping("/users/{userId}/reservations")
    @Operation(
        summary = "Брони пользователя",
        description = "Возвращает список всех броней указанного пользователя"
    )
    public ResponseEntity<List<Reservation>> getUserReservations(
        @Parameter(description = "ID пользователя", example = "1")
        @PathVariable Long userId) {
        List<Reservation> reservations = demoService.getUserReservations(userId);
        if (reservations.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @GetMapping("/slots/available")
    @Operation(
        summary = "Свободные места",
        description = "Возвращает список всех свободных парковочных мест (occupied = false)"
    )
    public ResponseEntity<List<BaseParkingSlot>> getAvailableSlots() {
        List<BaseParkingSlot> slots = demoService.getAvailableSlots();
        if (slots.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(slots, HttpStatus.OK);
    }
}