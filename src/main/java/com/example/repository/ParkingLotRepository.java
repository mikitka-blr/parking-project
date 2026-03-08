package com.example.repository;

import com.example.model.ParkingLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long> {

    @Query("SELECT p FROM ParkingLot p LEFT JOIN FETCH p.slots")
    List<ParkingLot> findAllWithSlotsUsingFetch();
}