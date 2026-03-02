package com.example;

import com.example.model.ExtraService;
import com.example.model.ParkingLot;
import com.example.repository.ExtraServiceRepository;
import com.example.repository.ParkingLotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    // Добавлен статический логгер для замены System.out
    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);

    private final ParkingLotRepository lotRepo;
    private final ExtraServiceRepository serviceRepo;

    public DatabaseSeeder(ParkingLotRepository lotRepo, ExtraServiceRepository serviceRepo) {
        this.lotRepo = lotRepo;
        this.serviceRepo = serviceRepo;
    }

    @Override
    public void run(String... args) {
        ExtraService wash = new ExtraService("Car Wash", 20.0);
        serviceRepo.save(wash);

        ParkingLot lot = new ParkingLot();
        lot.setName("Grand Center");
        lotRepo.save(lot);

        // Исправлено: логирование вместо вывода в консоль
        logger.info("Database seeding completed successfully.");
    }
}