package com.example.service;

import com.example.model.User;
import com.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final com.example.repository.ReservationRepository reservationRepository;
    private final com.example.repository.BaseParkingSlotRepository slotRepository;

    public UserService(UserRepository userRepository,
                       com.example.repository.ReservationRepository reservationRepository,
                       com.example.repository.BaseParkingSlotRepository slotRepository) {
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
        this.slotRepository = slotRepository;
    }

    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User findByIdentifier(String identifier) {
        if (identifier == null) return null;
        String id = identifier.trim();
        User byEmail = userRepository.findByEmail(id).orElse(null);
        if (byEmail != null) return byEmail;
        User byName = userRepository.findByFullNameIgnoreCase(id).orElse(null);
        return byName;
    }

    @Transactional
    public User updateUser(Long id, User userDetails) {
        return userRepository.findById(id)
            .map(user -> {
                user.setFullName(userDetails.getFullName());
                user.setEmail(userDetails.getEmail());
                return userRepository.save(user);
            })
            .orElse(null);
    }

    @Transactional
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            List<com.example.model.Reservation> userReservations = reservationRepository.findByUserId(id);
            for (com.example.model.Reservation res : userReservations) {
                com.example.model.BaseParkingSlot slot = res.getSlot();
                if (slot != null) {
                    slot.setOccupied(false);
                    slotRepository.save(slot);
                }
                reservationRepository.delete(res);
            }
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}