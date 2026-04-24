package com.example.controller;

import com.example.dto.BookingRequest;
import com.example.service.AsyncTaskService;
import com.example.service.CounterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/async")
public class AsyncController {

    private final AsyncTaskService asyncTaskService;
    private final CounterService counterService;

    public AsyncController(AsyncTaskService asyncTaskService, CounterService counterService) {
        this.asyncTaskService = asyncTaskService;
        this.counterService = counterService;
    }
    
    @PostMapping("/bulk")
    public ResponseEntity<Map<String, String>> submitBulkBooking(@RequestBody List<BookingRequest> requests) {
        String taskId = asyncTaskService.submitBulkBooking();
        asyncTaskService.processBulkBookingAsync(taskId, requests); 
        
        Map<String, String> response = new HashMap<>();
        response.put("taskId", taskId);
        response.put("status", "SUBMITTED");
        return ResponseEntity.accepted().body(response);
    }

    @GetMapping("/bulk/{taskId}")
    public ResponseEntity<Map<String, String>> getBulkBookingStatus(@PathVariable String taskId) {
        String status = asyncTaskService.getTaskStatus(taskId);
        Map<String, String> response = new HashMap<>();
        response.put("taskId", taskId);
        response.put("status", status);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/race-condition")
    public ResponseEntity<Map<String, Integer>> testRaceCondition(@RequestParam(defaultValue = "1000") int increments) {
        counterService.reset();
        
        try (ExecutorService executor = Executors.newFixedThreadPool(50)) {
            for (int i = 0; i < increments; i++) {
                executor.submit(() -> {
                    counterService.incrementUnsafe();
                    counterService.incrementSafe();
                });
            }
            
            executor.shutdown();
            try {
                executor.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        Map<String, Integer> results = new HashMap<>();
        results.put("expected", increments);
        results.put("unsafeCounterResult", counterService.getUnsafeCounter());
        results.put("safeCounterResult", counterService.getSafeCounter());
        
        return ResponseEntity.ok(results);
    }
}
