package com.group22.mobility.service;

import com.group22.mobility.dto.MaintenanceCostRow;
import com.group22.mobility.dto.MaintenanceLogDto;
import com.group22.mobility.dto.MongoMaintenanceCostRow;
import com.group22.mobility.model.MaintenanceLog;
import com.group22.mobility.model.Technician;
import com.group22.mobility.model.Vehicle;
import com.group22.mobility.model.mongodb.VehicleMaintenanceDocument;
import com.group22.mobility.model.mongodb.VehicleMaintenanceDocument.MaintenanceEntry;
import com.group22.mobility.model.mongodb.VehicleMaintenanceDocument.StationInfo;
import com.group22.mobility.repository.mariadb.MaintenanceLogRepository;
import com.group22.mobility.repository.mariadb.TechnicianRepository;
import com.group22.mobility.repository.mariadb.VehicleRepository;
import com.group22.mobility.repository.mongodb.VehicleMaintenanceRepository;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class Student1Service {

    // ── MariaDB repositories ──────────────────────────────────
    private final MaintenanceLogRepository logRepo;
    private final VehicleRepository vehicleRepo;
    private final TechnicianRepository techRepo;

    // ── MongoDB repository ────────────────────────────────────
    private final VehicleMaintenanceRepository mongoRepo;

    // ════════════════════════════════════════════════════════
    // MARIADB — Use Case + Analytics
    // ════════════════════════════════════════════════════════

    /** Use Case: Report Vehicle Damage → INSERT into Maintenance_Log (MariaDB) */
    @Transactional
    public MaintenanceLog reportDamage(MaintenanceLogDto dto) {
        Vehicle vehicle = vehicleRepo.findById(dto.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Vehicle not found: " + dto.getVehicleId()));

        Technician tech = techRepo.findById(dto.getTechnicianId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Technician not found: " + dto.getTechnicianId()));

        int nextLogNumber = logRepo.nextLogNumber(vehicle.getId());

        MaintenanceLog log = new MaintenanceLog();
        log.setLogNumber(nextLogNumber);
        log.setRepairDate(dto.getRepairDate());
        log.setServiceCost(dto.getServiceCost());
        log.setVehicle(vehicle);
        log.setTechnician(tech);

        return logRepo.save(log);
    }

    /** Analytics: Maintenance Cost per Station (MariaDB) */
    public List<MaintenanceCostRow> getMaintenanceCostReport() {
        return logRepo.maintenanceCostPerStation()
                .stream()
                .map(row -> new MaintenanceCostRow(
                        (String) row[0],
                        (String) row[1],
                        (String) row[2],
                        ((Number) row[3]).longValue(),
                        toBigDecimal(row[4]),
                        toBigDecimal(row[5])))
                .toList();
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepo.findAll();
    }

    public List<Technician> getAllTechnicians() {
        return techRepo.findAll();
    }

    // ════════════════════════════════════════════════════════
    // MONGODB — Migration
    // ════════════════════════════════════════════════════════

    /**
     * Migrate all data from MariaDB → MongoDB.
     * Reads every Vehicle + its Station + its Maintenance_Logs
     * and writes them as embedded documents into vehicle_maintenance collection.
     *
     * Safe to call multiple times — clears the collection first.
     */
    public int migrateToMongo() {
        // Step 1 — clear MongoDB (outside JPA transaction)
        mongoRepo.deleteAll();

        // Step 2 — read from MariaDB inside a transaction
        return doMigration();
    }

    @Transactional(readOnly = true)
    protected int doMigration() {
        List<Vehicle> vehicles = vehicleRepo.findAllWithDetails();

        for (Vehicle v : vehicles) {
            VehicleMaintenanceDocument doc = new VehicleMaintenanceDocument();
            doc.setVin(v.getVin());
            doc.setModelType(v.getModelType());

            if (v.getStation() != null) {
                doc.setStation(new StationInfo(
                        v.getStation().getStreetAddress(),
                        v.getStation().getGpsCoordinates()));
            }

            List<MaintenanceEntry> entries = v.getMaintenanceLogs()
                    .stream()
                    .map(ml -> new MaintenanceEntry(
                            ml.getLogNumber(),
                            ml.getRepairDate(),
                            ml.getServiceCost(),
                            ml.getTechnician().getEmployeeId(),
                            ml.getTechnician().getEmployee().getName()))
                    .toList();

            doc.setMaintenanceLogs(entries);
            mongoRepo.save(doc);
            VehicleMaintenanceDocument saved = mongoRepo.findByVin(v.getVin()).orElse(null);
            System.out.println(
                    "SAVED VIN=" + v.getVin()
                            + ", mongo logs=" + (saved == null ? "null" : saved.getMaintenanceLogs().size()));
        }

        return vehicles.size();
    }

    // ════════════════════════════════════════════════════════
    // MONGODB — Use Case
    // ════════════════════════════════════════════════════════

    /**
     * Use Case (MongoDB version): Add a maintenance log entry
     * directly to the embedded array inside the vehicle document.
     * Uses $push via findByVin + save pattern.
     */
    public VehicleMaintenanceDocument reportDamageMongo(MaintenanceLogDto dto) {
        // Need vehicle VIN to find the MongoDB document
        Vehicle vehicle = vehicleRepo.findById(dto.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Vehicle not found: " + dto.getVehicleId()));

        Technician tech = techRepo.findById(dto.getTechnicianId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Technician not found: " + dto.getTechnicianId()));

        VehicleMaintenanceDocument doc = mongoRepo.findByVin(vehicle.getVin())
                .orElseThrow(() -> new IllegalStateException(
                        "Vehicle not in MongoDB yet. Please run migration first."));

        // Calculate next log number for this vehicle
        int nextLogNumber = doc.getMaintenanceLogs().size() + 1;

        MaintenanceEntry entry = new MaintenanceEntry(
                nextLogNumber,
                dto.getRepairDate(),
                dto.getServiceCost(),
                tech.getEmployeeId(),
                tech.getEmployee().getName());

        doc.getMaintenanceLogs().add(entry);
        return mongoRepo.save(doc);
    }

    // ════════════════════════════════════════════════════════
    // MONGODB — Analytics
    // ════════════════════════════════════════════════════════

    /** Analytics: Maintenance Cost per Station (MongoDB aggregation pipeline) */
    public List<MongoMaintenanceCostRow> getMongoMaintenanceCostReport() {
        return mongoRepo.maintenanceCostPerStation();
    }

    private Long toLong(Object value) {
        if (value == null)
            return 0L;
        if (value instanceof Long l)
            return l;
        if (value instanceof Integer i)
            return i.longValue();
        if (value instanceof Number n)
            return n.longValue();
        return 0L;
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null)
            return BigDecimal.ZERO;
        if (value instanceof BigDecimal bd)
            return bd;
        // MongoDB Decimal128
        if (value instanceof org.bson.types.Decimal128 d)
            return d.bigDecimalValue();
        if (value instanceof Double d)
            return BigDecimal.valueOf(d);
        if (value instanceof Integer i)
            return BigDecimal.valueOf(i);
        if (value instanceof Long l)
            return BigDecimal.valueOf(l);
        return new BigDecimal(value.toString());
    }

    /** Check if MongoDB collection has data */
    public long getMongoDocumentCount() {
        return mongoRepo.count();
    }
}
