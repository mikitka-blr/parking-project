package com.example.service;

import com.example.model.ParkingLot;
import com.example.model.User;
import com.example.repository.ParkingLotRepository;
import com.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class DemoService {

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
        List<ParkingLot> lots = parkingLotRepository.findAll();
        for (ParkingLot lot : lots) {
            System.out.println("Парковка: " + lot.getName() + ", слотов: " + lot.getSlots().size());
        }
    }

    public void demonstrateSolutionWithJoinFetch() {
        List<ParkingLot> lots = parkingLotRepository.findAllWithSlotsUsingFetch();
        for (ParkingLot lot : lots) {
            System.out.println("Парковка: " + lot.getName() + ", слотов: " + lot.getSlots().size());
        }
    }
}