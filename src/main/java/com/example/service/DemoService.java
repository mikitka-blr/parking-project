package com.example.service;

import com.example.dto.BookingRequest;
import com.example.model.BaseParkingSlot;
import com.example.model.ExtraService;
import com.example.model.ParkingLot;
import com.example.model.Reservation;
import com.example.model.User;
import com.example.repository.BaseParkingSlotRepository;
import com.example.repository.ExtraServiceRepository;
import com.example.repository.ParkingLotRepository;
import com.example.repository.ReservationRepository;
import com.example.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class DemoService {

    private static final Logger LOG = LoggerFactory.getLogger(DemoService.class);

    private final UserRepository userRepository;
    private final ParkingLotRepository parkingLotRepository;
    private final ReservationRepository reservationRepository;
    private final BaseParkingSlotRepository slotRepository;
    private final ExtraServiceRepository extraServiceRepository;

    public DemoService(
        UserRepository userRepository,
        ParkingLotRepository parkingLotRepository,
        ReservationRepository reservationRepository,
        BaseParkingSlotRepository slotRepository,
        ExtraServiceRepository extraServiceRepository) {
        this.userRepository = userRepository;
        this.parkingLotRepository = parkingLotRepository;
        this.reservationRepository = reservationRepository;
        this.slotRepository = slotRepository;
        this.extraServiceRepository = extraServiceRepository;
    }

    public void failedTransactionDemo(User user) {
        userRepository.save(user);
        ParkingLot invalidLot = new ParkingLot(null, null);
        parkingLotRepository.save(invalidLot);
    }

    @Transactional
    public void successTransactionDemo(User user) {
        userRepository.save(user);
        ParkingLot lot = new ParkingLot("Центральная парковка", "ул. Мира, 1");
        parkingLotRepository.save(lot);
    }

    public void demonstrateNPlusOneProblem() {
        LOG.info("ПРОБЛЕМА N+1");
        List<ParkingLot> lots = parkingLotRepository.findAll();
        for (ParkingLot lot : lots) {
            LOG.info("Парковка: {}, слотов: {}",
                lot.getName(), lot.getSlots().size());
        }
    }

    public void demonstrateSolution() {
        LOG.info("РЕШЕНИЕ N+1");
        List<ParkingLot> lots = parkingLotRepository.findAllWithSlots(); // теперь работает
        for (ParkingLot lot : lots) {
            LOG.info("Парковка: {}, слотов: {}",
                lot.getName(), lot.getSlots().size());
        }
    }

    @Transactional
    public Reservation bookSlot(BookingRequest request) {
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        BaseParkingSlot slot = slotRepository.findById(request.getSlotId())
            .orElseThrow(() -> new RuntimeException("Место не найдено"));

        if (slot.isOccupied()) {
            throw new RuntimeException("Место уже занято");
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

        return reservationRepository.save(reservation);
    }

    public List<Reservation> getUserReservations(Long userId) {
        return reservationRepository.findByUserId(userId);
    }

    public List<BaseParkingSlot> getAvailableSlots() {
        return slotRepository.findByOccupied(false);
    }
}