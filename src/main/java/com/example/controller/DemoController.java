package com.example.service;

import com.example.model.ParkingLot;
import com.example.model.User;
import com.example.repository.ParkingLotRepository;
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

    public DemoService(UserRepository userRepository, ParkingLotRepository parkingLotRepository) {
        this.userRepository = userRepository;
        this.parkingLotRepository = parkingLotRepository;
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
        LOG.info("=== ДЕМОНСТРАЦИЯ ПРОБЛЕМЫ N+1 ===");
        List<ParkingLot> lots = parkingLotRepository.findAll();
        for (ParkingLot lot : lots) {
            LOG.info("Парковка: {}, слотов: {}", lot.getName(), lot.getSlots().size());
        }
        LOG.info("=== КОНЕЦ ДЕМОНСТРАЦИИ ===");
    }

    public void demonstrateSolutionWithJoinFetch() {
        LOG.info("=== РЕШЕНИЕ N+1 С JOIN FETCH ===");
        List<ParkingLot> lots = parkingLotRepository.findAllWithSlotsUsingFetch();
        for (ParkingLot lot : lots) {
            LOG.info("Парковка: {}, слотов: {}", lot.getName(), lot.getSlots().size());
        }
        LOG.info("=== КОНЕЦ ДЕМОНСТРАЦИИ ===");
    }
}