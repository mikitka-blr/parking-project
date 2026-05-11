package com.example.controller;

import com.example.dto.BookingRequest;
import com.example.model.BaseParkingSlot;
import com.example.model.Reservation;
import com.example.model.ExtraService;
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
    
    public DemoController(DemoService demoService) {
        this.demoService = demoService;
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

    @PostMapping("/book/bulk-tx")
    @Operation(summary = "Массовое бронирование (Транзакционно)",
        description = "Добавляет несколько броней. В случае ошибки на одной из них, все изменения откатываются.")
    public ResponseEntity<List<Reservation>> bookSlotsBulkTx(@Valid @RequestBody List<BookingRequest> requests) {
        List<Reservation> reservations = demoService.bookSlotsBulkTransactional(requests);
        return new ResponseEntity<>(reservations, HttpStatus.CREATED);
    }

    @PostMapping("/book/bulk-notx")
    @Operation(summary = "Массовое бронирование (Без транзакции на весь список)",
        description = "Добавляет несколько броней. В случае ошибки на середине списка, предыдущие успеют сохраниться.")
    public ResponseEntity<List<Reservation>> bookSlotsBulkNoTx(@Valid @RequestBody List<BookingRequest> requests) {
        List<Reservation> reservations = demoService.bookSlotsBulkNonTransactional(requests);
        return new ResponseEntity<>(reservations, HttpStatus.CREATED);
    }

    @PostMapping("/free-slot/{slotId}")
    @Operation(summary = "Освободить место", description = "Удаляет все активные брони для места и освобождает его")
    public ResponseEntity<Void> freeSlot(@Parameter(description = "ID места", example = "1")
                                             @PathVariable Long slotId) {
        demoService.freeSlot(slotId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/book/{reservationId}")
    @Operation(summary = "Обновить бронь", description = "Обновляет время активной брони")
    public ResponseEntity<Reservation> updateReservation(@PathVariable Long reservationId, @Valid @RequestBody BookingRequest request) {
        Reservation reservation = demoService.updateReservation(reservationId, request);
        return new ResponseEntity<>(reservation, HttpStatus.OK);
    }

    @GetMapping("/reservations")
    @Operation(summary = "Все бронирования")
    public ResponseEntity<List<Reservation>> getAllReservations() {
        List<Reservation> reservations = demoService.getAllReservations();
        if (reservations.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(reservations, HttpStatus.OK);
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

    @GetMapping("/slots")
    @Operation(
        summary = "Все места",
        description = "Возвращает список всех парковочных мест (включая занятые)"
    )
    public ResponseEntity<List<BaseParkingSlot>> getAllSlots() {
        List<BaseParkingSlot> slots = demoService.getAllSlots();
        if (slots.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(slots, HttpStatus.OK);
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

    @GetMapping("/services")
    @Operation(summary = "Дополнительные услуги", description = "Возвращает все дополнительные услуги")
    public ResponseEntity<List<ExtraService>> getExtraServices() {
        List<ExtraService> services = demoService.getAllExtraServices();
        if (services.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(services, HttpStatus.OK);
    }
}