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
import com.example.model.RegularParkingSlot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DemoServiceTest {

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

        lenient().when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        lenient().when(slotRepository.findById(1L)).thenReturn(Optional.of(slot1));
        lenient().when(slotRepository.findById(2L)).thenReturn(Optional.of(slot2));
        lenient().when(reservationRepository.save(any())).thenReturn(new Reservation());
    }

    @Test
    void testBookSlotsBulk_Success() {
        BookingRequest request = new BookingRequest(1L, 1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), null);
        List<Reservation> reservations = demoService.bookSlotsBulkTransactional(List.of(request));
        
        assertEquals(1, reservations.size());
        verify(reservationRepository, times(1)).save(any());
    }

    @Test
    void testBookSlotsBulk_ThrowsException_OnOccupiedSlot() {
        BookingRequest req1 = new BookingRequest(1L, 1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), null);
        BookingRequest req2 = new BookingRequest(1L, 2L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), null);

        assertThrows(SlotAlreadyOccupiedException.class, () -> {
            demoService.bookSlotsBulkNonTransactional(Arrays.asList(req1, req2));
        });

        // First one is saved (in non-tx method it would be saved sequentially up to the exception, but wait, both these methods actually just map through a stream which throws immediately when map applies the function and it hits the exception)
        verify(reservationRepository, times(1)).save(any());
    }
}




