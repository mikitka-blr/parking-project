package com.example.service;

import com.example.dto.ParkingSlotDTO;
import com.example.mapper.ParkingMapper;
import com.example.model.BaseParkingSlot;
import com.example.model.Reservation;
import com.example.model.User;
import com.example.repository.BaseParkingSlotRepository;
import com.example.repository.UserRepository;
import com.example.repository.ReservationRepository;
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

    // Тот самый метод, который не мог найти контроллер
    public List<ParkingSlotDTO> getSlotsByStatus(Boolean occupied) {
        return slotRepository.findAll().stream()
            .filter(s -> occupied == null || s.isOccupied() == occupied)
            .map(mapper::toDTO)
            .toList();
    }

    @Transactional
    public Reservation bookSlot(Long slotId, User user) {
        User savedUser = userRepository.save(user);

        BaseParkingSlot slot = slotRepository.findById(slotId)
            .orElseThrow(() -> new IllegalArgumentException("Slot not found with id: " + slotId));

        if (slot.isOccupied()) {
            throw new IllegalStateException("Slot is already occupied");
        }

        slot.setOccupied(true);
        slotRepository.save(slot);

        Reservation res = new Reservation();
        res.setUser(savedUser);
        res.setSlot(slot);

        return reservationRepository.save(res);
    }
}