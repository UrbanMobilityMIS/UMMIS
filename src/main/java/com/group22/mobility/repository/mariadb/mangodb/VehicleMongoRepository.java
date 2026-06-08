package com.group22.mobility.repository.mariadb.mangodb;

import com.group22.mobility.model.mangodb.VehicleDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleMongoRepository
        extends MongoRepository<VehicleDocument, String> {

    Optional<VehicleDocument> findByVehicleId(Integer vehicleId);
}