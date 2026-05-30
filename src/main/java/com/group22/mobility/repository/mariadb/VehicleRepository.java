package com.group22.mobility.repository.mariadb;

import com.group22.mobility.model.Vehicle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {

    // Show only available vehicles
    List<Vehicle> findByAvailableTrue();
}