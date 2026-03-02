package com.example.service;

import com.example.dto.ParkingSlotDTO;
import com.example.mapper.ParkingMapper;
import com.example.model.BaseParkingSlot;
import com.example.model.Reservation;
import com.example.model.User;
import com.example.repository.BaseParkingSlotRepository;
import com.example.repository.ReservationRepository;
import com.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ParkingService {

    private final BaseParkingSlotRepository slotRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final ParkingMapper mapper;

    public ParkingService(BaseParkingSlotRepository slotRepository,
                          UserRepository userRepository,
                          ReservationRepository reservationRepository,
                          ParkingMapper mapper) {
        this.slotRepository = slotRepository;
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
        this.mapper = mapper;
    }

    @Transactional
    public Reservation bookSlot(Long slotId, User user) {
        // 1. Ищем место
        BaseParkingSlot slot = slotRepository.findById(slotId)
            .orElseThrow(() -> new RuntimeException("Место не найдено"));

        if (slot.isOccupied()) {
            throw new RuntimeException("Место уже занято");
        }

        // 2. Сохраняем пользователя
        User savedUser = userRepository.save(user);

        // 3. Занимаем место
        slot.setOccupied(true);
        slotRepository.save(slot);

        // 4. Создаем бронь
        Reservation reservation = new Reservation();
        reservation.setUser(savedUser);
        reservation.setSlot(slot);

        return reservationRepository.save(reservation);
    }

    public List<ParkingSlotDTO> getSlotsByStatus(Boolean occupied) {
        return slotRepository.findAll().stream()
            .filter(slot -> occupied == null || slot.isOccupied() == occupied)
            .map(mapper::toDTO)
            .toList();
    }

    public ParkingSlotDTO getSlotById(Long id) {
        return slotRepository.findById(id)
            .map(mapper::toDTO)
            .orElse(null);
    }
}