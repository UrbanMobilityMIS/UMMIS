package com.group22.mobility.repository.mongodb;

import com.group22.mobility.model.mongodb.TechnicianMaintenanceDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TechnicianMaintenanceDocumentRepository
        extends MongoRepository<TechnicianMaintenanceDocument, String> {

    List<TechnicianMaintenanceDocument> findByCertificationLevelOrderByRepairDateDesc(String certificationLevel);
}