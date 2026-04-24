package com.example.service;

import com.example.dto.BookingRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AsyncTaskService {

    private static final Logger LOG = LoggerFactory.getLogger(AsyncTaskService.class);
    
    // Хранение статусов задач
    private final Map<String, String> taskStatuses = new ConcurrentHashMap<>();
    
    private final DemoService demoService;
    private final CounterService counterService;

    public AsyncTaskService(DemoService demoService, CounterService counterService) {
        this.demoService = demoService;
        this.counterService = counterService;
    }

    public String submitBulkBooking(List<BookingRequest> requests) {
        String taskId = UUID.randomUUID().toString();
        taskStatuses.put(taskId, "SUBMITTED");
        return taskId;
    }

    @Async("taskExecutor")
    public void processBulkBookingAsync(String taskId, List<BookingRequest> requests) {
        taskStatuses.put(taskId, "IN_PROGRESS");
        LOG.info("Запуск задачи {} в потоке {}", taskId, Thread.currentThread().getName());
        
        try {
            // Имитация долгой операции (например, сложной обработки, проверок 5 секунд)
            Thread.sleep(20000);
            
            // Вызов транзакционного метода (булк операция) 
            demoService.bookSlotsBulkTransactional(requests);
            
            // Увеличиваем счетчики
            for (int i = 0; i < requests.size(); i++) {
                counterService.incrementUnsafe();
                counterService.incrementSafe();
            }

            taskStatuses.put(taskId, "COMPLETED");
            LOG.info("Задача {} успешно завершена", taskId);
        } catch (Exception e) {
            LOG.error("Ошибка при выполнении задачи {}: {}", taskId, e.getMessage());
            taskStatuses.put(taskId, "FAILED");
        }
    }

    public String getTaskStatus(String taskId) {
        return taskStatuses.getOrDefault(taskId, "NOT_FOUND");
    }
}
