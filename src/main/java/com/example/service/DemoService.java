package com.example.service;

import com.example.dto.BookingRequest;
import com.example.exception.SlotAlreadyOccupiedException;
import com.example.exception.SlotNotFoundException;
import com.example.exception.UserNotFoundException;
import com.example.model.BaseParkingSlot;
import com.example.model.ExtraService;
import com.example.model.Reservation;
import com.example.model.User;
import com.example.repository.BaseParkingSlotRepository;
import com.example.repository.ExtraServiceRepository;
import com.example.repository.ReservationRepository;
import com.example.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DemoService {

    private static final Logger LOG = LoggerFactory.getLogger(DemoService.class);

    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final BaseParkingSlotRepository slotRepository;
    private final ExtraServiceRepository extraServiceRepository;
    private final ReservationCacheService cacheService;

    public DemoService(
        UserRepository userRepository,
        ReservationRepository reservationRepository,
        BaseParkingSlotRepository slotRepository,
        ExtraServiceRepository extraServiceRepository,
        ReservationCacheService cacheService) {
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
        this.slotRepository = slotRepository;
        this.extraServiceRepository = extraServiceRepository;
        this.cacheService = cacheService;
    }

    @Transactional
    public Reservation bookSlot(BookingRequest request) {
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new UserNotFoundException("Пользователь с id " + request.getUserId() + " не найден"));

        BaseParkingSlot slot = slotRepository.findById(request.getSlotId())
            .orElseThrow(() -> new SlotNotFoundException("Место с id " + request.getSlotId() + " не найдено"));

        if (slot.isOccupied()) {
            throw new SlotAlreadyOccupiedException("Место " + slot.getNumber() + " уже занято");
        }

        Reservation reservation = new Reservation(
            user,
            slot,
            request.getStartTime(),
            request.getEndTime()
        );

        if (request.getServiceIds() != null && !request.getServiceIds().isEmpty()) {
            List<ExtraService> services = extraServiceRepository.findAllById(request.getServiceIds());
            reservation.setServices(services);
        }

        slot.setOccupied(true);
        slotRepository.save(slot);

        Reservation saved = reservationRepository.save(reservation);

        cacheService.clearCache();
        LOG.info("Кэш очищен после добавления новой брони");

        return saved;
    }

    @Transactional
    public List<Reservation> bookSlotsBulkTransactional(List<BookingRequest> requests) {
        return java.util.Optional.ofNullable(requests)
            .orElseGet(java.util.Collections::emptyList)
            .stream()
            .map(this::bookSlot)
            .toList();
    }

    public List<Reservation> bookSlotsBulkNonTransactional(List<BookingRequest> requests) {
        return java.util.Optional.ofNullable(requests)
            .orElseGet(java.util.Collections::emptyList)
            .stream()
            .map(this::bookSlot)
            .toList();
    }

    public List<BaseParkingSlot> getAllSlots() {
        return slotRepository.findAll();
    }

    public Page<Reservation> searchReservationsWithCache(
        String name,
        LocalDateTime startDate,
        int page,
        int size) {

        Page<Reservation> cached = cacheService.getPageFromCache(name, startDate, page, size);
        if (cached != null) {
            LOG.info("Данные получены из КЭША");
            return cached;
        }

        LOG.info("Данных нет в кэше, запрос в БД");
        Pageable pageable = PageRequest.of(page, size);

        Page<Reservation> result = reservationRepository.findReservationsWithPagination(
            name, startDate, pageable);

        cacheService.putInCache(name, startDate, page, size, result.getContent());

        return result;
    }

    public List<Reservation> searchReservationsJPQL(String name, LocalDateTime startDate) {
        return reservationRepository.findReservationsByUserNameAndDate(name, startDate);
    }

    public List<Reservation> searchReservationsNative(String name, LocalDateTime startDate) {
        return reservationRepository.findReservationsNative(name, startDate);
    }

    public List<Reservation> getUserReservations(Long userId) {
        return reservationRepository.findByUserId(userId);
    }

    public List<BaseParkingSlot> getAvailableSlots() {
        return slotRepository.findByOccupied(false);
    }

    public int getCacheSize() {
        return cacheService.cacheSize();
    }
}