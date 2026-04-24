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

    // 1. Асинхронный запуск операции (Возвращает ID задачи)
    @PostMapping("/bulk")
    public ResponseEntity<Map<String, String>> submitBulkBooking(@RequestBody List<BookingRequest> requests) {
        String taskId = asyncTaskService.submitBulkBooking(requests);
        asyncTaskService.processBulkBookingAsync(taskId, requests); // Вызов через прокси Спринга (@Async сработает)
        
        Map<String, String> response = new HashMap<>();
        response.put("taskId", taskId);
        response.put("status", "SUBMITTED");
        return ResponseEntity.accepted().body(response);
    }

    // 1.1 Получить статус задачи
    @GetMapping("/bulk/{taskId}")
    public ResponseEntity<Map<String, String>> getBulkBookingStatus(@PathVariable String taskId) {
        String status = asyncTaskService.getTaskStatus(taskId);
        Map<String, String> response = new HashMap<>();
        response.put("taskId", taskId);
        response.put("status", status);
        return ResponseEntity.ok(response);
    }

    // 3. Демонстрация Race Condition (Многопоточное обращение к счетчикам)
    @PostMapping("/race-condition")
    public ResponseEntity<Map<String, Integer>> testRaceCondition(@RequestParam(defaultValue = "1000") int increments) {
        counterService.reset();
        
        // Создаем пул на 50 потоков (ExecutorService)
        ExecutorService executor = Executors.newFixedThreadPool(50);
        
        for (int i = 0; i < increments; i++) {
            executor.submit(() -> {
                counterService.incrementUnsafe(); // Небезопасный счётчик
                counterService.incrementSafe();   // Потокобезопасный счётчик (AtomicInteger)
            });
        }
        
        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        Map<String, Integer> results = new HashMap<>();
        results.put("expected", increments);
        results.put("unsafeCounterResult", counterService.getUnsafeCounter());
        results.put("safeCounterResult", counterService.getSafeCounter());
        
        return ResponseEntity.ok(results);
    }
}
