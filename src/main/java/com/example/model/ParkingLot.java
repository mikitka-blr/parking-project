package com.example.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "parking_lots")
public class ParkingLot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;

    // Связь OneToMany: Одна парковка содержит много слотов.
    // FetchType.LAZY: Слоты не грузятся из базы сразу (оптимизация).
    // CascadeType.ALL: При удалении парковки удаляются и её слоты.
    @OneToMany(mappedBy = "parkingLot", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BaseParkingSlot> slots = new ArrayList<>();

    public ParkingLot() {

    }

    public ParkingLot(String name, String address) {
        this.name = name;
        this.address = address;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id; }
    public String getName() {
        return name; }
    public void setName(String name) {
        this.name = name; }
    public String getAddress() {
        return address; }
    public void setAddress(String address) {
        this.address = address; }
    public List<BaseParkingSlot> getSlots() {
        return slots; }
    public void setSlots(List<BaseParkingSlot> slots) {
        this.slots = slots; }
}