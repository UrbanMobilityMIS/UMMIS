package com.group22.mobility.service;

import com.group22.mobility.dto.Level2TechnicianReportRow;
import com.group22.mobility.dto.OnboardTechnicianDto;
import com.group22.mobility.model.Employee;
import com.group22.mobility.model.MaintenanceLog;
import com.group22.mobility.model.Technician;
import com.group22.mobility.model.Vehicle;
import com.group22.mobility.repository.mariadb.EmployeeRepository;
import com.group22.mobility.repository.mariadb.MaintenanceLogRepository;
import com.group22.mobility.repository.mariadb.TechnicianRepository;
import com.group22.mobility.repository.mariadb.VehicleRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class Student3Service {

    private final EmployeeRepository employeeRepository;
    private final TechnicianRepository technicianRepository;
    private final VehicleRepository vehicleRepository;
    private final MaintenanceLogRepository maintenanceLogRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public MaintenanceLog onboardTechnician(OnboardTechnicianDto dto) {

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        Employee employee = new Employee();
        employee.setName(dto.getEmployeeName());
        employee.setHireDate(dto.getHireDate());

        Employee savedEmployee = employeeRepository.save(employee);

        Technician technician = new Technician();
        technician.setEmployee(savedEmployee);
        technician.setToolkitId(dto.getToolkitId());
        technician.setCertificationLevel("Level 2");

        Technician savedTechnician = technicianRepository.save(technician);

        Integer nextLogNumber = maintenanceLogRepository
                .findAll()
                .stream()
                .filter(log -> log.getVehicle().getId().equals(vehicle.getId()))
                .map(MaintenanceLog::getLogNumber)
                .max(Integer::compareTo)
                .orElse(0) + 1;

        MaintenanceLog log = new MaintenanceLog();
        log.setLogNumber(nextLogNumber);
        log.setRepairDate(dto.getRepairDate());
        log.setServiceCost(dto.getServiceCost());
        log.setVehicle(vehicle);
        log.setTechnician(savedTechnician);

        return maintenanceLogRepository.save(log);
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public List<Level2TechnicianReportRow> getAnalytics() {

    String jpql = """
        SELECT t.employeeId,
               e.name,
               t.certificationLevel,
               COUNT(m.id),
               SUM(m.serviceCost),
               AVG(m.serviceCost),
               MAX(m.repairDate)
        FROM MaintenanceLog m
        JOIN m.technician t
        JOIN t.employee e
        WHERE t.certificationLevel = 'Level 2'
        GROUP BY t.employeeId, e.name, t.certificationLevel
        ORDER BY SUM(m.serviceCost) DESC
    """;

    List<Object[]> results = entityManager
            .createQuery(jpql, Object[].class)
            .getResultList();

    return results.stream()
            .map(row -> new Level2TechnicianReportRow(
                    (Integer) row[0],
                    (String) row[1],
                    (String) row[2],
                    (Long) row[3],
                    (java.math.BigDecimal) row[4],
                    java.math.BigDecimal.valueOf((Double) row[5]),
                    (java.time.LocalDate) row[6]
            ))
            .toList();
}
}