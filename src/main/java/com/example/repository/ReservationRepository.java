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

    // 1. Сложный JPQL запрос с фильтрацией по вложенной сущности
    @Query("SELECT r FROM Reservation r "
        + "JOIN r.user u "
        + "WHERE u.fullName LIKE %:name% "
        + "AND r.startTime >= :startDate")
    List<Reservation> findReservationsByUserNameAndDate(
        @Param("name") String name,
        @Param("startDate") LocalDateTime startDate);

    // 2. Аналогичный запрос через native query
    @Query(value = "SELECT r.* FROM reservations r "
        + "JOIN users u ON r.user_id = u.id "
        + "WHERE u.full_name ILIKE CONCAT('%', :name, '%') "
        + "AND r.start_time >= :startDate",
        nativeQuery = true)
    List<Reservation> findReservationsNative(
        @Param("name") String name,
        @Param("startDate") LocalDateTime startDate);

    // 3. JPQL с пагинацией
    @Query("SELECT r FROM Reservation r "
        + "JOIN r.user u "
        + "WHERE u.fullName LIKE %:name% "
        + "AND r.startTime >= :startDate")
    Page<Reservation> findReservationsWithPagination(
        @Param("name") String name,
        @Param("startDate") LocalDateTime startDate,
        Pageable pageable);

    // Native query с пагинацией
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