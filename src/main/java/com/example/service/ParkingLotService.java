package com.example.service;

import com.example.model.ParkingLot;
import com.example.repository.ParkingLotRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ParkingLotService {

    private final ParkingLotRepository parkingLotRepository;

    public ParkingLotService(ParkingLotRepository parkingLotRepository) {
        this.parkingLotRepository = parkingLotRepository;
    }

    public List<ParkingLot> getAllParkingLots() {
        return parkingLotRepository.findAll();
    }

    public ParkingLot getParkingLotById(Long id) {
        return parkingLotRepository.findById(id).orElse(null);
    }
}