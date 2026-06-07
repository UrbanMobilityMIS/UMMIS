package com.group22.mobility.repository.mariadb;

import com.group22.mobility.model.Vehicle;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {

    @Query("SELECT DISTINCT v FROM Vehicle v LEFT JOIN FETCH v.maintenanceLogs ml LEFT JOIN FETCH ml.technician t LEFT JOIN FETCH t.employee LEFT JOIN FETCH v.station")
    List<Vehicle> findAllWithDetails();
}
