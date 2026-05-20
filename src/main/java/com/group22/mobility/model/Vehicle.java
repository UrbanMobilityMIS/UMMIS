package com.group22.mobility.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "vehicle")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String vin;

    @Column(name = "battery_level")
    private Integer batteryLevel;

    @Column(name = "model_type")
    private String modelType;

    @ManyToOne
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @OneToMany(mappedBy = "vehicle")
    private List<MaintenanceLog> maintenanceLogs;
}
