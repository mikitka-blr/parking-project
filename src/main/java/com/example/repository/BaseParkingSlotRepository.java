package com.example.repository;

import com.example.model.BaseParkingSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaseParkingSlotRepository extends JpaRepository<BaseParkingSlot, Long> {
}