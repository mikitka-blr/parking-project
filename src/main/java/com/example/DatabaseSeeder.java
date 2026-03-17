package com.example;

import com.example.model.BaseParkingSlot;
import com.example.model.DisabledParkingSlot;
import com.example.model.ElectricParkingSlot;
import com.example.model.ExtraService;
import com.example.model.ParkingLot;
import com.example.model.RegularParkingSlot;
import com.example.model.Reservation;
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
import java.time.LocalDateTime;
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
        createParkingLot();
        createTestUsers();
        createTestReservations();
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
            new ExtraService("Мойка", 15.0),
            new ExtraService("Шиномонтаж", 20.0),
            new ExtraService("Зарядка", 10.0)
        );
        extraServiceRepository.saveAll(services);
        LOG.info("Создано {} услуг", services.size());
    }

    private void createParkingLot() {
        ParkingLot lot = new ParkingLot(
            "Центральная парковка",
            "г. Минск, ул. Центральная, 1"
        );

        lot.addSlot(new RegularParkingSlot("A1", false, true));
        lot.addSlot(new RegularParkingSlot("A2", false, true));
        lot.addSlot(new RegularParkingSlot("A3", true, false));
        lot.addSlot(new ElectricParkingSlot("E1", false, 50));
        lot.addSlot(new ElectricParkingSlot("E2", true, 75));
        lot.addSlot(new DisabledParkingSlot("D1", false, true));

        parkingLotRepository.save(lot);
        LOG.info("Создана парковка с {} местами", lot.getSlots().size());
    }

    private void createTestUsers() {
        List<User> users = Arrays.asList(
            new User("Иван Петров", "ivan@example.com"),
            new User("Мария Сидорова", "maria@example.com")
        );

        users.get(0).setPhone("+375-29-111-11-11");
        users.get(1).setPhone("+375-33-222-22-22");

        for (User user : users) {
            if (userRepository.findByEmail(user.getEmail()).isEmpty()) {
                userRepository.save(user);
                LOG.info("Создан пользователь: {}", user.getEmail());
            }
        }
    }

    private void createTestReservations() {
        List<User> users = userRepository.findAll();
        List<BaseParkingSlot> slots = slotRepository.findAll();
        List<ExtraService> services = extraServiceRepository.findAll();

        if (users.isEmpty() || slots.isEmpty() || services.isEmpty()) {
            return;
        }

        User user = users.get(0);
        BaseParkingSlot slot = slots.stream()
            .filter(s -> !s.isOccupied())
            .findFirst()
            .orElse(null);

        if (slot != null) {
            Reservation reservation = new Reservation(
                user,
                slot,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(3)
            );

            reservation.setServices(List.of(services.get(0)));

            slot.setOccupied(true);
            slotRepository.save(slot);

            reservationRepository.save(reservation);
            LOG.info("Создана тестовая бронь для {}", user.getFullName());
        }
    }
}