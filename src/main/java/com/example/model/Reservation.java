package com.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.List;

@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "slot_id")
    private BaseParkingSlot slot;

    // Связь ManyToMany: Одно бронирование может содержать много услуг.
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "reservation_services",
        joinColumns = @JoinColumn(name = "reservation_id"),
        inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<ExtraService> services;

    public Reservation() {

    }

    public Long getId() {
        return id; }
    public User getUser() {
        return user; }
    public void setUser(User user) {
        this.user = user; }
    public BaseParkingSlot getSlot() {
        return slot; }
    public void setSlot(BaseParkingSlot slot) {
        this.slot = slot; }
    public List<ExtraService> getServices() {
        return services; }
    public void setServices(List<ExtraService> services) {
        this.services = services; }
}