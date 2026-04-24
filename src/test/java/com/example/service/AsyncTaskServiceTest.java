package com.example.service;

import com.example.dto.BookingRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsyncTaskServiceTest {

    @Mock
    private DemoService demoService;

    @Mock
    private CounterService counterService;

    @InjectMocks
    private AsyncTaskService asyncTaskService;

    @Test
    void submitBulkBooking_ShouldReturnTaskIdAndSetSubmitted() {
        String taskId = asyncTaskService.submitBulkBooking();
        
        assertNotNull(taskId);
        assertEquals("SUBMITTED", asyncTaskService.getTaskStatus(taskId));
    }

    @Test
    void getTaskStatus_WhenMissing_ShouldReturnNotFound() {
        assertEquals("NOT_FOUND", asyncTaskService.getTaskStatus("invalid_id"));
    }

    @Test
    void processBulkBookingAsync_Success() {
        String taskId = "test-task-1";
        List<BookingRequest> requests = new ArrayList<>();
        requests.add(new BookingRequest());
        
        asyncTaskService.processBulkBookingAsync(taskId, requests);
        
        verify(demoService, times(1)).bookSlotsBulkTransactional(requests);
        verify(counterService, times(1)).incrementUnsafe();
        verify(counterService, times(1)).incrementSafe();
        
        assertEquals("COMPLETED", asyncTaskService.getTaskStatus(taskId));
    }

    @Test
    void processBulkBookingAsync_WhenException_ShouldSetFailed() {
        String taskId = "test-task-2";
        List<BookingRequest> requests = new ArrayList<>();
        
        doThrow(new RuntimeException("Test Exception")).when(demoService).bookSlotsBulkTransactional(requests);
        
        asyncTaskService.processBulkBookingAsync(taskId, requests);
        
        assertEquals("FAILED", asyncTaskService.getTaskStatus(taskId));
    }

    @Test
    void processBulkBookingAsync_WhenThreadInterrupted_ShouldSetFailedAndInterrupt() {
        String taskId = "test-task-3";
        List<BookingRequest> requests = new ArrayList<>();
        
        Thread.currentThread().interrupt();
        
        asyncTaskService.processBulkBookingAsync(taskId, requests);
        
        assertEquals("FAILED", asyncTaskService.getTaskStatus(taskId));
        assertTrue(Thread.interrupted());
    }
}
