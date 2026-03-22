package com.example.mapper;

import com.example.dto.ReservationDTO;
import com.example.model.Reservation;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReservationMapper {

    public ReservationDTO toDTO(Reservation reservation) {
        if (reservation == null) {
            return null;
        }

        ReservationDTO dto = new ReservationDTO();
        dto.setId(reservation.getId());

        if (reservation.getUser() != null) {
            dto.setUserId(reservation.getUser().getId());
            dto.setUserName(reservation.getUser().getFullName());
        }

        if (reservation.getSlot() != null) {
            dto.setSlotId(reservation.getSlot().getId());
            dto.setSlotNumber(reservation.getSlot().getNumber());
        }

        dto.setStartTime(reservation.getStartTime());
        dto.setEndTime(reservation.getEndTime());

        return dto;
    }

    public List<ReservationDTO> toDTOList(List<Reservation> reservations) {
        return reservations.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
}