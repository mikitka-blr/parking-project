package com.example.service;

import com.example.model.BaseParkingSlot;
import com.example.repository.BaseParkingSlotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingSlotServiceTest {

    @Mock
    private BaseParkingSlotRepository slotRepository;

    @InjectMocks
    private ParkingSlotService slotService;

    private BaseParkingSlot slot;

    @BeforeEach
    void setUp() {
        slot = mock(BaseParkingSlot.class);
        lenient().when(slot.getId()).thenReturn(1L);
        lenient().when(slot.isOccupied()).thenReturn(false);
        lenient().when(slot.getSlotType()).thenReturn("REGULAR");
    }

    @Test
    void getAllSlots() {
        when(slotRepository.findAll()).thenReturn(List.of(slot));
        List<BaseParkingSlot> result = slotService.getAllSlots();
        assertEquals(1, result.size());
    }

    @Test
    void getSlotById_Found() {
        when(slotRepository.findById(1L)).thenReturn(Optional.of(slot));
        BaseParkingSlot result = slotService.getSlotById(1L);
        assertNotNull(result);
    }

    @Test
    void getSlotById_NotFound() {
        when(slotRepository.findById(1L)).thenReturn(Optional.empty());
        BaseParkingSlot result = slotService.getSlotById(1L);
        assertNull(result);
    }

    @Test
    void getAvailableSlots() {
        // Создаем занятый и свободный слот для проверки обеих веток лямбды
        BaseParkingSlot freeSlot = mock(BaseParkingSlot.class);
        lenient().when(freeSlot.isOccupied()).thenReturn(false);

        BaseParkingSlot occupiedSlot = mock(BaseParkingSlot.class);
        lenient().when(occupiedSlot.isOccupied()).thenReturn(true);

        when(slotRepository.findAll()).thenReturn(List.of(freeSlot, occupiedSlot));
        
        List<BaseParkingSlot> result = slotService.getAvailableSlots();
        assertEquals(1, result.size());
        assertFalse(result.get(0).isOccupied());
    }

    @Test
    void getSlotsByType() {
        when(slotRepository.findAll()).thenReturn(List.of(slot));
        List<BaseParkingSlot> result = slotService.getSlotsByType("REGULAR");
        assertEquals(1, result.size());
        
        List<BaseParkingSlot> result2 = slotService.getSlotsByType("VIP");
        assertEquals(0, result2.size());
    }

    @Test
    void getSlotsByType_NullTypeInList() {
        // Добавляем слот, у которого getSlotType() вернет null
        BaseParkingSlot slotWithNullType = mock(BaseParkingSlot.class);
        lenient().when(slotWithNullType.getId()).thenReturn(2L);
        lenient().when(slotWithNullType.getSlotType()).thenReturn(null);

        when(slotRepository.findAll()).thenReturn(List.of(slotWithNullType, slot));

        // Вызываем со значением типа
        List<BaseParkingSlot> result = slotService.getSlotsByType("REGULAR");
        assertEquals(1, result.size());
    }

    @Test
    void getSlotsByType_NullType() {
        when(slotRepository.findAll()).thenReturn(List.of(slot));
        // Когда тип null, падает NullPointerException в getSlotsByType при equalsIgnoreCase
        // Или если Spring Data возвращает null (на самом деле equalsIgnoreCase от null падает).
        // Если ничего не падает, значит type не валидируется и вернет пустой список, так как getType() == null
        List<BaseParkingSlot> result = slotService.getSlotsByType(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void occupySlot_Found() {
        when(slotRepository.findById(1L)).thenReturn(Optional.of(slot));
        when(slotRepository.save(any(BaseParkingSlot.class))).thenReturn(slot);
        
        BaseParkingSlot result = slotService.occupySlot(1L);
        
        assertNotNull(result);
        verify(slot).setOccupied(true);
        verify(slotRepository).save(slot);
    }

    @Test
    void occupySlot_NotFound() {
        when(slotRepository.findById(1L)).thenReturn(Optional.empty());
        BaseParkingSlot result = slotService.occupySlot(1L);
        assertNull(result);
        verify(slotRepository, never()).save(any());
    }

    @Test
    void occupySlot_lambda_coverage() {
        BaseParkingSlot s = new com.example.model.RegularParkingSlot();
        s.setId(5L);
        s.setOccupied(false);
        when(slotRepository.findById(5L)).thenReturn(Optional.of(s));
        when(slotRepository.save(s)).thenReturn(s);
        
        BaseParkingSlot result = slotService.occupySlot(5L);
        
        assertNotNull(result);
        assertTrue(result.isOccupied());
    }

    @Test
    void freeSlot_Found() {
        when(slotRepository.findById(1L)).thenReturn(Optional.of(slot));
        when(slotRepository.save(any(BaseParkingSlot.class))).thenReturn(slot);
        
        BaseParkingSlot result = slotService.freeSlot(1L);
        
        assertNotNull(result);
        verify(slot).setOccupied(false);
        verify(slotRepository).save(slot);
    }

    @Test
    void freeSlot_NotFound() {
        when(slotRepository.findById(1L)).thenReturn(Optional.empty());
        BaseParkingSlot result = slotService.freeSlot(1L);
        assertNull(result);
        verify(slotRepository, never()).save(any());
    }

    @Test
    void freeSlot_lambda_coverage() {
        BaseParkingSlot s = new com.example.model.RegularParkingSlot();
        s.setId(5L);
        s.setOccupied(true);
        when(slotRepository.findById(5L)).thenReturn(Optional.of(s));
        when(slotRepository.save(s)).thenReturn(s);
        
        BaseParkingSlot result = slotService.freeSlot(5L);
        
        assertNotNull(result);
        assertFalse(result.isOccupied());
    }
}
