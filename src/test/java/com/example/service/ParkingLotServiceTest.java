package com.example.service;

import com.example.model.ParkingLot;
import com.example.repository.ParkingLotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParkingLotServiceTest {

    @Mock
    private ParkingLotRepository parkingLotRepository;

    @InjectMocks
    private ParkingLotService parkingLotService;

    private ParkingLot parkingLot;

    @BeforeEach
    void setUp() {
        parkingLot = new ParkingLot();
        parkingLot.setId(1L);
        parkingLot.setName("Main Lot");
        parkingLot.setAddress("123 Main St");
    }

    @Test
    void getAllParkingLots() {
        when(parkingLotRepository.findAll()).thenReturn(List.of(parkingLot));
        List<ParkingLot> result = parkingLotService.getAllParkingLots();
        assertEquals(1, result.size());
    }

    @Test
    void getParkingLotById_Found() {
        when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(parkingLot));
        ParkingLot result = parkingLotService.getParkingLotById(1L);
        assertNotNull(result);
    }

    @Test
    void getParkingLotById_NotFound() {
        when(parkingLotRepository.findById(1L)).thenReturn(Optional.empty());
        ParkingLot result = parkingLotService.getParkingLotById(1L);
        assertNull(result);
    }
}

