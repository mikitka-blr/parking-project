package com.example.repository;

import com.example.model.ParkingLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long> {

    default Optional<ParkingLot> findMainParkingLot() {
        List<ParkingLot> lots = findAll();
        return lots.isEmpty() ? Optional.empty() : Optional.of(lots.get(0));
    }
}