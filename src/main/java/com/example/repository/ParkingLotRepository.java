package com.example.repository;

import com.example.model.ParkingLot;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long> {
    // Решение N+1: за один запрос достаем парковки и их слоты через JOIN
    @Override
    @EntityGraph(attributePaths = {"slots"})
    List<ParkingLot> findAll();
}