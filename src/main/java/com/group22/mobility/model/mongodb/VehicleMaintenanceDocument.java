package com.group22.mobility.model.mongodb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.Decimal128;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * MongoDB document for Student 1 use case.
 *
 * NoSQL Design Decision:
 * Instead of 3 normalized tables (Vehicle, Station, Maintenance_Log),
 * all related data is embedded in one document.
 * This eliminates JOINs and demonstrates the document-oriented approach.
 *
 * Collection: vehicle_maintenance
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "vehicle_maintenance")
public class VehicleMaintenanceDocument {

    @Id
    private String id;

    @Indexed
    private String vin;

    private String modelType;

    // Embedded Station (replaces FK to Station table)
    private StationInfo station;

    // Embedded Maintenance Logs array (replaces Maintenance_Log table)
    private List<MaintenanceEntry> maintenanceLogs = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StationInfo {
        private String streetAddress;
        private String gpsCoordinates;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MaintenanceEntry {
        private Integer logNumber;
        private LocalDate repairDate;
        private Decimal128 serviceCost; // ← Decimal128 for proper MongoDB numeric aggregation
        private Integer technicianId;
        private String technicianName;

        // Convenience constructor accepting BigDecimal
        public MaintenanceEntry(Integer logNumber, LocalDate repairDate,
                BigDecimal serviceCost,
                Integer technicianId, String technicianName) {
            this.logNumber = logNumber;
            this.repairDate = repairDate;
            this.serviceCost = serviceCost != null
                    ? new Decimal128(serviceCost)
                    : new Decimal128(BigDecimal.ZERO);
            this.technicianId = technicianId;
            this.technicianName = technicianName;
        }

        // Getter returns BigDecimal for convenience
        public BigDecimal getServiceCostAsBigDecimal() {
            return serviceCost != null ? serviceCost.bigDecimalValue() : BigDecimal.ZERO;
        }
    }
}
