package com.group22.mobility.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "station")
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "street_address", nullable = false)
    private String streetAddress;

    @Column(name = "gps_coordinates", nullable = false)
    private String gpsCoordinates;

    @Column(name = "total_docks", nullable = false)
    private Integer totalDocks;

    @OneToMany(mappedBy = "station")
    private List<Vehicle> vehicles;
}
