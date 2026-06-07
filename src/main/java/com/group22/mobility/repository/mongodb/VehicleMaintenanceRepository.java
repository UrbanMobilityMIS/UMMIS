package com.group22.mobility.repository.mongodb;

import com.group22.mobility.dto.MongoMaintenanceCostRow;
import com.group22.mobility.model.mongodb.VehicleMaintenanceDocument;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleMaintenanceRepository
        extends MongoRepository<VehicleMaintenanceDocument, String> {

    // Find by VIN (used during migration + use case)
    Optional<VehicleMaintenanceDocument> findByVin(String vin);

    // Analytics: maintenance cost per station
    // Unwinds the embedded logs array, then groups by station + vehicle
    @Aggregation(pipeline = {
            "{ $unwind: '$maintenanceLogs' }",
            "{ $group: { " +
                    "_id: { stationAddress: '$station.streetAddress', vin: '$vin', modelType: '$modelType' }, " +
                    "totalLogs: { $sum: 1 }, " +
                    "totalCost: { $sum: '$maintenanceLogs.serviceCost' }, " +
                    "avgCost: { $avg: '$maintenanceLogs.serviceCost' }" +
                    "} }",
            "{ $project: { " +
                    "_id: 0, " +
                    "stationAddress: '$_id.stationAddress', " +
                    "vehicleVin: '$_id.vin', " +
                    "vehicleModel: '$_id.modelType', " +
                    "totalLogs: 1, totalCost: 1, avgCost: 1" +
                    "} }",
            "{ $sort: { totalCost: -1 } }"
    })
    List<MongoMaintenanceCostRow> maintenanceCostPerStation();
}
