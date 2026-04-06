package com.example.service;

import com.example.dto.BookingRequest;
import com.example.exception.SlotAlreadyOccupiedException;
import com.example.model.BaseParkingSlot;
import com.example.model.Reservation;
import com.example.model.User;
import com.example.repository.BaseParkingSlotRepository;
import com.example.repository.ExtraServiceRepository;
import com.example.repository.ReservationRepository;
import com.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DemoServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private BaseParkingSlotRepository slotRepository;
    @Mock
    private ExtraServiceRepository extraServiceRepository;
    @Mock
    private ReservationCacheService cacheService;

    @InjectMocks
    private DemoService demoService;

    private User user;
    private BaseParkingSlot slot1;
    private BaseParkingSlot slot2;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        slot1 = mock(BaseParkingSlot.class);
        lenient().when(slot1.getId()).thenReturn(1L);
        lenient().when(slot1.isOccupied()).thenReturn(false);

        slot2 = mock(BaseParkingSlot.class);
        lenient().when(slot2.getId()).thenReturn(2L);
        lenient().when(slot2.isOccupied()).thenReturn(true);
    }

    @Test
    void testBookSlot_Success_WithServices() {
        BookingRequest request = new BookingRequest(1L, 1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), List.of(1L, 2L));
        lenient().when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        lenient().when(slotRepository.findById(1L)).thenReturn(Optional.of(slot1));
        lenient().when(extraServiceRepository.findAllById(request.getServiceIds())).thenReturn(Collections.emptyList());
        lenient().when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> i.getArgument(0));

        Reservation result = demoService.bookSlot(request);

        assertNotNull(result);
        verify(slot1).setOccupied(true);
        verify(slotRepository).save(slot1);
        verify(reservationRepository).save(any(Reservation.class));
        verify(cacheService).clearCache();
    }

    @Test
    void testBookSlot_Success_WithoutServices() {
        BookingRequest request = new BookingRequest(1L, 1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), Collections.emptyList());
        lenient().when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        lenient().when(slotRepository.findById(1L)).thenReturn(Optional.of(slot1));
        lenient().when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> i.getArgument(0));

        Reservation result = demoService.bookSlot(request);

        assertNotNull(result);
        verify(slot1).setOccupied(true);
        verify(slotRepository).save(slot1);
        verify(reservationRepository).save(any(Reservation.class));
        verify(cacheService).clearCache();
    }

    @Test
    void testBookSlot_UserNotFound() {
        BookingRequest request = new BookingRequest(999L, 1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), null);
        lenient().when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> demoService.bookSlot(request));
    }

    @Test
    void testBookSlot_SlotNotFound() {
        BookingRequest request = new BookingRequest(1L, 999L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), null);
        lenient().when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        lenient().when(slotRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> demoService.bookSlot(request));
    }

    @Test
    void testBookSlot_SlotAlreadyOccupied() {
        BookingRequest request = new BookingRequest(1L, 2L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), null);
        lenient().when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        lenient().when(slotRepository.findById(2L)).thenReturn(Optional.of(slot2));

        assertThrows(SlotAlreadyOccupiedException.class, () -> demoService.bookSlot(request));
    }

    @Test
    void testBookSlotsBulk_Success() {
        BookingRequest request = new BookingRequest(1L, 1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), null);
        lenient().when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        lenient().when(slotRepository.findById(1L)).thenReturn(Optional.of(slot1));
        lenient().when(reservationRepository.save(any())).thenReturn(new Reservation());

        List<Reservation> reservations = demoService.bookSlotsBulkTransactional(List.of(request));
        
        assertEquals(1, reservations.size());
        verify(reservationRepository, times(1)).save(any());
    }

    @Test
    void testBookSlotsBulkTransactional_NullInput() {
        List<Reservation> res = demoService.bookSlotsBulkTransactional(null);
        assertTrue(res.isEmpty());
    }

    @Test
    void testBookSlotsBulkNonTransactional_NullInput() {
        List<Reservation> res = demoService.bookSlotsBulkNonTransactional(null);
        assertTrue(res.isEmpty());
    }

    @Test
    void testBookSlotsBulk_ThrowsException_OnOccupiedSlot() {
        lenient().when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        lenient().when(slotRepository.findById(1L)).thenReturn(Optional.of(slot1));
        lenient().when(slotRepository.findById(2L)).thenReturn(Optional.of(slot2));
        lenient().when(reservationRepository.save(any())).thenReturn(new Reservation());

        BookingRequest req1 = new BookingRequest(1L, 1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), null);
        BookingRequest req2 = new BookingRequest(1L, 2L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), null);
        List<BookingRequest> requests = Arrays.asList(req1, req2);

        assertThrows(SlotAlreadyOccupiedException.class, () -> demoService.bookSlotsBulkNonTransactional(requests));

        verify(reservationRepository, times(1)).save(any());
    }

    @Test
    void testGetAllSlots() {
        demoService.getAllSlots();
        verify(slotRepository).findAll();
    }

    @Test
    void testSearchReservationsWithCache_Cached() {
        Page<Reservation> page = new PageImpl<>(List.of(new Reservation()));
        when(cacheService.getPageFromCache(anyString(), any(), anyInt(), anyInt())).thenReturn(page);
        
        Page<Reservation> result = demoService.searchReservationsWithCache("Test", LocalDateTime.now(), 0, 10);
        assertNotNull(result);
        verify(reservationRepository, never()).findReservationsWithPagination(anyString(), any(), any());
    }

    @Test
    void testSearchReservationsWithCache_NotCached() {
        when(cacheService.getPageFromCache(anyString(), any(), anyInt(), anyInt())).thenReturn(null);
        Page<Reservation> page = new PageImpl<>(List.of(new Reservation()));
        when(reservationRepository.findReservationsWithPagination(anyString(), any(), any(Pageable.class))).thenReturn(page);
        
        Page<Reservation> result = demoService.searchReservationsWithCache("Test", LocalDateTime.now(), 0, 10);
        assertNotNull(result);
        verify(cacheService).putInCache(anyString(), any(), anyInt(), anyInt(), anyList());
    }

    @Test
    void testSearchReservationsJPQL() {
        demoService.searchReservationsJPQL("nm", null);
        verify(reservationRepository).findReservationsByUserNameAndDate("nm", null);
    }

    @Test
    void testSearchReservationsNative() {
        demoService.searchReservationsNative("nm", null);
        verify(reservationRepository).findReservationsNative("nm", null);
    }

    @Test
    void testGetUserReservations() {
        demoService.getUserReservations(1L);
        verify(reservationRepository).findByUserId(1L);
    }

    @Test
    void testGetAvailableSlots() {
        demoService.getAvailableSlots();
        verify(slotRepository).findByOccupied(false);
    }

    @Test
    void testGetCacheSize() {
        demoService.getCacheSize();
        verify(cacheService).cacheSize();
    }
}
