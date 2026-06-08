package com.group22.mobility.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MongoTechnicianUseCaseDto {

    // Employee / technician business information
    private String employeeName;
    private String toolkitId;
    private String certificationLevel = "Level 2";

    // Maintenance task information
    private LocalDate repairDate;
    private BigDecimal serviceCost;

    // Vehicle information
    private String vehicleVin;
    private String vehicleModel;
}