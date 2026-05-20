package com.group22.mobility.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

// ── Form DTO (Use Case input) ─────────────────────────────────
@Data
public class MaintenanceLogDto {

    @NotNull(message = "Vehicle is required")
    private Integer vehicleId;

    @NotNull(message = "Technician is required")
    private Integer technicianId;

    @NotNull(message = "Repair date is required")
    private LocalDate repairDate;

    @NotNull(message = "Service cost is required")
    @Positive(message = "Service cost must be positive")
    private BigDecimal serviceCost;
}
