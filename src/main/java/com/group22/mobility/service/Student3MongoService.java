package com.group22.mobility.service;

import com.group22.mobility.model.MaintenanceLog;
import com.group22.mobility.model.mongodb.TechnicianMaintenanceDocument;
import com.group22.mobility.repository.mariadb.MaintenanceLogRepository;
import com.group22.mobility.repository.mariadb.mongodb.TechnicianMaintenanceDocumentRepository;import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.group22.mobility.dto.Level2TechnicianMongoReportRow;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

import java.util.List;

@Service
@RequiredArgsConstructor
public class Student3MongoService {

    private final MaintenanceLogRepository maintenanceLogRepository;
    private final TechnicianMaintenanceDocumentRepository mongoRepository;

    public void migrateStudent3DataToMongo() {

        List<MaintenanceLog> logs = maintenanceLogRepository.findAll();

        List<TechnicianMaintenanceDocument> documents = logs.stream()
                .filter(log -> log.getTechnician() != null)
                .filter(log -> "Level 2".equals(log.getTechnician().getCertificationLevel()))
                .map(log -> new TechnicianMaintenanceDocument(
                        null,
                        log.getTechnician().getEmployeeId(),
                        log.getTechnician().getEmployee().getName(),
                        log.getTechnician().getCertificationLevel(),
                        log.getTechnician().getToolkitId(),
                        log.getLogNumber(),
                        log.getRepairDate(),
                        log.getServiceCost(),
                        log.getVehicle().getId(),
                        log.getVehicle().getVin(),
                        log.getVehicle().getModelType()
                ))
                .toList();

        mongoRepository.deleteAll();
        mongoRepository.saveAll(documents);
    }

    public List<Level2TechnicianMongoReportRow> getLevel2TechnicianReport() {

    List<TechnicianMaintenanceDocument> documents =
            mongoRepository.findByCertificationLevelOrderByRepairDateDesc("Level 2");

    return documents.stream()
            .collect(Collectors.groupingBy(TechnicianMaintenanceDocument::getTechnicianId))
            .values()
            .stream()
            .map(group -> {
                TechnicianMaintenanceDocument first = group.get(0);

                BigDecimal totalCost = group.stream()
                        .map(TechnicianMaintenanceDocument::getServiceCost)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal averageCost = group.isEmpty()
                        ? BigDecimal.ZERO
                        : totalCost.divide(
                                BigDecimal.valueOf(group.size()),
                                2,
                                RoundingMode.HALF_UP
                        );

                return new Level2TechnicianMongoReportRow(
                        first.getTechnicianId(),
                        first.getTechnicianName(),
                        first.getCertificationLevel(),
                        (long) group.size(),
                        totalCost,
                        averageCost,
                        group.stream()
                                .map(TechnicianMaintenanceDocument::getRepairDate)
                                .max(LocalDate::compareTo)
                                .orElse(null)
                );
            })
            .sorted(Comparator.comparing(Level2TechnicianMongoReportRow::getTotalCost).reversed())
            .toList();
    }
}