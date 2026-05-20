package com.group22.mobility.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class MaintenanceCostRow {
    private String stationAddress;
    private String vehicleVin;
    private String vehicleModel;
    private Long   totalLogs;
    private BigDecimal totalCost;
    private BigDecimal avgCost;
}
