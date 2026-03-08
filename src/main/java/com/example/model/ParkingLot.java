package com.example.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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

    @Column(nullable = false, length = 255)
    private String name;

    private String address;

    @OneToMany(mappedBy = "parkingLot",
        cascade = CascadeType.ALL,
        fetch = FetchType.LAZY,
        orphanRemoval = true)
    private List<BaseParkingSlot> slots = new ArrayList<>();

    public ParkingLot() {
    }

    public ParkingLot(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<BaseParkingSlot> getSlots() {
        return slots;
    }

    public void setSlots(List<BaseParkingSlot> slots) {
        this.slots = slots;
    }

    public int getTotalSpaces() {
        return slots.size();
    }

    public void addSlot(BaseParkingSlot slot) {
        slots.add(slot);
        slot.setParkingLot(this);
    }

    public void removeSlot(BaseParkingSlot slot) {
        slots.remove(slot);
        slot.setParkingLot(null);
    }
}