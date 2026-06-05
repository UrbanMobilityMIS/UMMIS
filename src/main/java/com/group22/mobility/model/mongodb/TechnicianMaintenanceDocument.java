package com.group22.mobility.model.mongodb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "student3_technician_maintenance")
public class TechnicianMaintenanceDocument {

    @Id
    private String id;

    private Integer technicianId;
    private String technicianName;
    private String certificationLevel;
    private String toolkitId;

    private Integer logNumber;
    private LocalDate repairDate;
    private BigDecimal serviceCost;

    private Integer vehicleId;
    private String vehicleVin;
    private String vehicleModel;
}