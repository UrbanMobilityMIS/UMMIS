package com.group22.mobility.repository.mongodb;

import com.group22.mobility.model.mongodb.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserMongoRepository
        extends MongoRepository<UserDocument, String> {

    Optional<UserDocument> findByUserId(Integer userId);
}