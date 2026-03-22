package com.example.controller;

import com.example.dto.ReservationDTO;
import com.example.mapper.ReservationMapper;
import com.example.model.Reservation;
import com.example.service.DemoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/search")
public class ReservationSearchController {

    private final DemoService demoService;
    private final ReservationMapper reservationMapper;

    public ReservationSearchController(DemoService demoService, ReservationMapper reservationMapper) {
        this.demoService = demoService;
        this.reservationMapper = reservationMapper;
    }

    @GetMapping("/reservations")
    public ResponseEntity<Page<ReservationDTO>> searchReservations(
        @RequestParam String name,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

        Page<Reservation> resultPage = demoService.searchReservationsWithCache(name, startDate, page, size);
        List<ReservationDTO> dtoList = reservationMapper.toDTOList(resultPage.getContent());
        Page<ReservationDTO> dtoPage = new PageImpl<>(dtoList, resultPage.getPageable(), resultPage.getTotalElements());

        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/reservations/jpql")
    public ResponseEntity<List<ReservationDTO>> searchReservationsJPQL(
        @RequestParam String name,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate) {

        List<Reservation> result = demoService.searchReservationsJPQL(name, startDate);
        List<ReservationDTO> dtoList = reservationMapper.toDTOList(result);
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/reservations/native")
    public ResponseEntity<List<ReservationDTO>> searchReservationsNative(
        @RequestParam String name,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate) {

        List<Reservation> result = demoService.searchReservationsNative(name, startDate);
        List<ReservationDTO> dtoList = reservationMapper.toDTOList(result);
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/cache/info")
    public ResponseEntity<String> getCacheInfo() {
        int cacheSize = demoService.getCacheSize();
        return ResponseEntity.ok("Размер кэша: " + cacheSize);
    }
}