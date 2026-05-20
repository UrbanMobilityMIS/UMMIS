package com.group22.mobility.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "maintenance_Log")
public class MaintenanceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "log_number", nullable = false)
    private Integer logNumber;

    @Column(name = "repair_date", nullable = false)
    private LocalDate repairDate;

    @Column(name = "service_cost")
    private BigDecimal serviceCost;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "technician_id", nullable = false)
    private Technician technician;
}
