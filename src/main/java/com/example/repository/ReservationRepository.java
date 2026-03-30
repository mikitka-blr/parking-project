package com.example.repository;

import com.example.model.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUserId(Long userId);

    List<Reservation> findBySlotId(Long slotId);

    @Query("SELECT r FROM Reservation r "
        + "JOIN FETCH r.slot s "
        + "JOIN FETCH r.user u "
        + "WHERE u.fullName LIKE %:name% "
        + "AND r.startTime >= :startDate")
    List<Reservation> findReservationsByUserNameAndDate(
        @Param("name") String name,
        @Param("startDate") LocalDateTime startDate);

    @Query(value = "SELECT r.* FROM reservations r "
        + "JOIN users u ON r.user_id = u.id "
        + "WHERE u.full_name ILIKE CONCAT('%', :name, '%') "
        + "AND r.start_time >= :startDate",
        nativeQuery = true)
    List<Reservation> findReservationsNative(
        @Param("name") String name,
        @Param("startDate") LocalDateTime startDate);

    @Query("SELECT r FROM Reservation r "
        + "JOIN FETCH r.slot s "
        + "JOIN FETCH r.user u "
        + "WHERE u.fullName LIKE %:name% "
        + "AND r.startTime >= :startDate")
    Page<Reservation> findReservationsWithPagination(
        @Param("name") String name,
        @Param("startDate") LocalDateTime startDate,
        Pageable pageable);

    @Query(value = "SELECT r.* FROM reservations r "
        + "JOIN users u ON r.user_id = u.id "
        + "WHERE u.full_name ILIKE CONCAT('%', :name, '%') "
        + "AND r.start_time >= :startDate",
        countQuery = "SELECT COUNT(*) FROM reservations r "
            + "JOIN users u ON r.user_id = u.id "
            + "WHERE u.full_name ILIKE CONCAT('%', :name, '%') "
            + "AND r.start_time >= :startDate",
        nativeQuery = true)

    Page<Reservation> findReservationsNativeWithPagination(
        @Param("name") String name,
        @Param("startDate") LocalDateTime startDate,
        Pageable pageable);
}