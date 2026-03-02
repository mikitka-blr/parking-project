package com.example;

import com.example.model.ParkingLot;
import com.example.model.RegularParkingSlot;
import com.example.repository.ParkingLotRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final ParkingLotRepository parkingLotRepository;

    public DatabaseSeeder(ParkingLotRepository parkingLotRepository) {
        this.parkingLotRepository = parkingLotRepository;
    }

    @Override
    public void run(String... args) {
        ParkingLot lot = new ParkingLot();
        lot.setName("Центральная парковка");
        lot.setAddress("ул. Ленина, 1");

        // Создаем готовые места
        RegularParkingSlot slot1 = new RegularParkingSlot("A-101", false, true);
        slot1.setParkingLot(lot);

        RegularParkingSlot slot2 = new RegularParkingSlot("A-102", false, true);
        slot2.setParkingLot(lot);

        RegularParkingSlot slot3 = new RegularParkingSlot("A-103", false, false);
        slot3.setParkingLot(lot);

        lot.getSlots().add(slot1);
        lot.getSlots().add(slot2);
        lot.getSlots().add(slot3);

        parkingLotRepository.save(lot);
        System.out.println(">>> База заполнена: создано 3 свободных места (ID: 1, 2, 3)");
    }
}