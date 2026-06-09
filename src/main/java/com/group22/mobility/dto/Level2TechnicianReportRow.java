package com.group22.mobility.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Level2TechnicianReportRow {

    private Integer technicianId;
    private String technicianName;
    private String certificationLevel;

    private Long totalTasks;
    private BigDecimal totalCost;
    private BigDecimal averageCost;
    private LocalDate lastRepairDate;
}