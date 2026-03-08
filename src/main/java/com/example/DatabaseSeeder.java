package com.example;

import com.example.model.BaseParkingSlot;
import com.example.model.DisabledParkingSlot;
import com.example.model.ElectricParkingSlot;
import com.example.model.ExtraService;
import com.example.model.ParkingLot;
import com.example.model.RegularParkingSlot;
import com.example.model.User;
import com.example.repository.BaseParkingSlotRepository;
import com.example.repository.ExtraServiceRepository;
import com.example.repository.ParkingLotRepository;
import com.example.repository.ReservationRepository;
import com.example.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;
import java.util.List;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseSeeder.class);

    private final ParkingLotRepository parkingLotRepository;
    private final ExtraServiceRepository extraServiceRepository;
    private final BaseParkingSlotRepository slotRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    public DatabaseSeeder(
        ParkingLotRepository parkingLotRepository,
        ExtraServiceRepository extraServiceRepository,
        BaseParkingSlotRepository slotRepository,
        ReservationRepository reservationRepository,
        UserRepository userRepository) {
        this.parkingLotRepository = parkingLotRepository;
        this.extraServiceRepository = extraServiceRepository;
        this.slotRepository = slotRepository;
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        clearDatabase();
        createExtraServices();
        createParkingLotWithSlots();
        createTestUsers();
        LOG.info("База данных инициализирована");
    }

    private void clearDatabase() {
        reservationRepository.deleteAll();
        userRepository.deleteAll();
        slotRepository.deleteAll();
        parkingLotRepository.deleteAll();
        extraServiceRepository.deleteAll();
    }

    private void createExtraServices() {
        List<ExtraService> services = Arrays.asList(
            new ExtraService("Мойка автомобиля", 15.0),
            new ExtraService("Шиномонтаж", 20.0),
            new ExtraService("Зарядка электромобиля", 10.0),
            new ExtraService("Пылесос", 5.0),
            new ExtraService("Подкачка шин", 3.0)
        );
        extraServiceRepository.saveAll(services);
        LOG.info("Создано {} дополнительных услуг", services.size());
    }

    private void createParkingLotWithSlots() {
        ParkingLot mainLot = new ParkingLot(
            "Центральная парковка",
            "г. Минск, ул. Центральная, 1"
        );

        List<BaseParkingSlot> slots = Arrays.asList(
            new RegularParkingSlot("A1", false, true),
            new RegularParkingSlot("A2", false, true),
            new RegularParkingSlot("A3", true, false),
            new RegularParkingSlot("A4", false, false),
            new RegularParkingSlot("A5", true, true),
            new ElectricParkingSlot("E1", false, 50),
            new ElectricParkingSlot("E2", true, 75),
            new ElectricParkingSlot("E3", false, 100),
            new DisabledParkingSlot("D1", false, true),
            new DisabledParkingSlot("D2", true, true)
        );

        for (BaseParkingSlot slot : slots) {
            slot.setParkingLot(mainLot);
        }

        parkingLotRepository.save(mainLot);

        LOG.info("Создана парковка: {} с {} местами", mainLot.getName(), slots.size());
    }

    private void createTestUsers() {
        List<User> users = Arrays.asList(
            new User("Иван Петров", "ivan@example.com"),
            new User("Мария Сидорова", "maria@example.com"),
            new User("Алексей Иванов", "alex@example.com")
        );

        users.get(0).setPhone("+375-29-111-11-11");
        users.get(1).setPhone("+375-33-222-22-22");
        users.get(2).setPhone("+375-25-333-33-33");

        userRepository.saveAll(users);
        LOG.info("Создано {} тестовых пользователей", users.size());
    }
}