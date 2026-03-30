package com.example.controller;

import com.example.dto.ReservationDTO;
import com.example.mapper.ReservationMapper;
import com.example.model.Reservation;
import com.example.service.DemoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Поиск броней", description = "Методы для поиска броней с фильтрацией, пагинацией и кэшированием")
public class ReservationSearchController {

    private final DemoService demoService;
    private final ReservationMapper reservationMapper;

    public ReservationSearchController(DemoService demoService, ReservationMapper reservationMapper) {
        this.demoService = demoService;
        this.reservationMapper = reservationMapper;
    }

    @GetMapping("/reservations/jpql")
    @Operation(
        summary = "Поиск броней (JPQL)",
        description = "Поиск броней по имени пользователя и дате начала с использованием JPQL. "
            + "Демонстрирует сложный запрос с фильтрацией по вложенной сущности."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешный поиск"),
        @ApiResponse(responseCode = "400", description = "Неверные параметры запроса"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<List<ReservationDTO>> searchReservationsJPQL(
        @Parameter(description = "Имя пользователя для фильтрации (поиск по части имени)", example = "Иван")
        @RequestParam String name,
        @Parameter(description = "Начальная дата (ISO формат)", example = "2026-03-01T00:00:00")
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate) {

        List<Reservation> result = demoService.searchReservationsJPQL(name, startDate);
        List<ReservationDTO> dtoList = reservationMapper.toDTOList(result);
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/reservations/native")
    @Operation(
        summary = "Поиск броней (Native SQL)",
        description = "Поиск броней по имени пользователя и дате начала с использованием нативного SQL запроса. "
            + "Демонстрирует аналогичный JPQL запрос на чистом SQL."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешный поиск"),
        @ApiResponse(responseCode = "400", description = "Неверные параметры запроса"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<List<ReservationDTO>> searchReservationsNative(
        @Parameter(description = "Имя пользователя для фильтрации (регистронезависимый поиск)", example = "Иван")
        @RequestParam String name,
        @Parameter(description = "Начальная дата (ISO формат)", example = "2026-03-01T00:00:00")
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate) {

        List<Reservation> result = demoService.searchReservationsNative(name, startDate);
        List<ReservationDTO> dtoList = reservationMapper.toDTOList(result);
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/reservations")
    @Operation(
        summary = "Поиск броней с пагинацией и кэшированием",
        description = "Поиск броней с фильтрацией по имени и дате. "
            + "Поддерживается пагинация (page, size) и in-memory кэширование на основе HashMap. "
            + "Первый запрос — в БД, повторные — из кэша."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешный поиск",
            content = @Content(schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "400", description = "Неверные параметры запроса"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<Page<ReservationDTO>> searchReservations(
        @Parameter(description = "Имя пользователя для фильтрации", example = "Иван")
        @RequestParam String name,
        @Parameter(description = "Начальная дата (ISO формат)", example = "2026-03-01T00:00:00")
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        @Parameter(description = "Номер страницы (начинается с 0)", example = "0")
        @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Размер страницы (количество записей на странице)", example = "10")
        @RequestParam(defaultValue = "10") int size) {

        Page<Reservation> resultPage = demoService.searchReservationsWithCache(name, startDate, page, size);
        List<ReservationDTO> dtoList = reservationMapper.toDTOList(resultPage.getContent());
        Page<ReservationDTO> dtoPage = new PageImpl<>(dtoList, resultPage.getPageable(), resultPage.getTotalElements());

        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/cache/info")
    @Operation(
        summary = "Информация о кэше",
        description = "Возвращает текущий размер in-memory кэша. "
            + "Используется для демонстрации работы кэширования и его инвалидации."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешно"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<String> getCacheInfo() {
        int cacheSize = demoService.getCacheSize();
        return ResponseEntity.ok("Размер кэша: " + cacheSize);
    }
}