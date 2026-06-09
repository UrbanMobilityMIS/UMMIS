package com.group22.mobility.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class OnboardTechnicianDto {

    // Employee
    private String employeeName;

    private LocalDate hireDate;

    // Technician
    private String toolkitId;

    private String certificationLevel;

    // Maintenance
    private LocalDate repairDate;

    private BigDecimal serviceCost;

    // Vehicle
    private Integer vehicleId;
}