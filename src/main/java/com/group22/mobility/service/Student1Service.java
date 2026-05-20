package com.group22.mobility.service;

import com.group22.mobility.dto.MaintenanceLogDto;
import com.group22.mobility.dto.MaintenanceCostRow;
import com.group22.mobility.model.MaintenanceLog;
import com.group22.mobility.model.Vehicle;
import com.group22.mobility.model.Technician;
import com.group22.mobility.repository.mariadb.MaintenanceLogRepository;
import com.group22.mobility.repository.mariadb.VehicleRepository;
import com.group22.mobility.repository.mariadb.TechnicianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class Student1Service {

    private final MaintenanceLogRepository logRepo;
    private final VehicleRepository vehicleRepo;
    private final TechnicianRepository techRepo;

    /** Use Case: Report Vehicle Damage → INSERT into Maintenance_Log */
    @Transactional
    public MaintenanceLog reportDamage(MaintenanceLogDto dto) {
        Vehicle vehicle = vehicleRepo.findById(dto.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + dto.getVehicleId()));

        Technician tech = techRepo.findById(dto.getTechnicianId())
                .orElseThrow(() -> new IllegalArgumentException("Technician not found: " + dto.getTechnicianId()));

        int nextLogNumber = logRepo.nextLogNumber(vehicle.getId());

        MaintenanceLog log = new MaintenanceLog();
        log.setLogNumber(nextLogNumber);
        log.setRepairDate(dto.getRepairDate());
        log.setServiceCost(dto.getServiceCost());
        log.setVehicle(vehicle);
        log.setTechnician(tech);

        return logRepo.save(log);
    }

    /** Analytics: Maintenance Cost per Station */
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

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal bd) {
            return bd;
        }
        if (value instanceof Number n) {
            return BigDecimal.valueOf(n.doubleValue());
        }
        return new BigDecimal(value.toString());
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepo.findAll();
    }

    public List<Technician> getAllTechnicians() {
        return techRepo.findAll();
    }
}
