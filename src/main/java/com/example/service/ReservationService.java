package com.example.service;

import com.example.model.Reservation;
import com.example.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public Reservation createReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id).orElse(null);
    }

    public List<Reservation> getReservationsByUserId(Long userId) {
        return reservationRepository.findAll().stream()
            .filter(r -> r.getUser().getId().equals(userId))
            .collect(Collectors.toList());
    }

    public List<Reservation> getReservationsBySlotId(Long slotId) {
        return reservationRepository.findAll().stream()
            .filter(r -> r.getSlot().getId().equals(slotId))
            .collect(Collectors.toList());
    }

    @Transactional
    public boolean deleteReservation(Long id) {
        if (reservationRepository.existsById(id)) {
            reservationRepository.deleteById(id);
            return true;
        }
        return false;
    }
}