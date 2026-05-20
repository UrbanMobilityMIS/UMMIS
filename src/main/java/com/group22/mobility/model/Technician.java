package com.group22.mobility.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "technician")
public class Technician {

    @Id
    @Column(name = "employee_id")
    private Integer employeeId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "toolkit_id")
    private String toolkitId;

    @Column(name = "certification_level")
    private String certificationLevel;
}
