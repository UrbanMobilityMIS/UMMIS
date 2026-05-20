package com.group22.mobility.repository.mariadb;

import com.group22.mobility.model.MaintenanceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MaintenanceLogRepository extends JpaRepository<MaintenanceLog, Integer> {

    @Query("SELECT COALESCE(MAX(m.logNumber), 0) + 1 FROM MaintenanceLog m WHERE m.vehicle.id = :vehicleId")
    Integer nextLogNumber(@Param("vehicleId") Integer vehicleId);

    @Query("""
        SELECT s.streetAddress,
               v.vin,
               v.modelType,
               COUNT(m.id),
               SUM(m.serviceCost),
               AVG(m.serviceCost)
        FROM MaintenanceLog m
        JOIN m.vehicle v
        JOIN v.station s
        WHERE m.serviceCost > 0
        GROUP BY s.id, s.streetAddress, v.id, v.vin, v.modelType
        ORDER BY SUM(m.serviceCost) DESC
    """)
    List<Object[]> maintenanceCostPerStation();
}
