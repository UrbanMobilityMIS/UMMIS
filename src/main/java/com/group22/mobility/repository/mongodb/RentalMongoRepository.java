package com.group22.mobility.repository.mongodb;

import com.group22.mobility.model.mongodb.RentalDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RentalMongoRepository
        extends MongoRepository<RentalDocument, String> {

    Optional<RentalDocument> findByRentalId(Integer rentalId);

    Optional<RentalDocument> findTopByOrderByRentalIdDesc();
}