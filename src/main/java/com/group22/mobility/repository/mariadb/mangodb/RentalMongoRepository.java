package com.group22.mobility.repository.mariadb.mangodb;

import com.group22.mobility.model.mangodb.RentalDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalMongoRepository
        extends MongoRepository<RentalDocument, String> {
}