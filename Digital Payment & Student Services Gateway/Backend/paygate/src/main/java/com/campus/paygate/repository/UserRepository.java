package com.campus.paygate.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.campus.paygate.model.User;

public interface UserRepository extends MongoRepository<User, String> {

    // Renamed the method so Spring doesn't try to parse 'Matric' as a variable
    @Query("{ '$or': [ { 'matricNo': ?0 }, { 'staffId': ?0 }, { 'email': ?0 } ] }")
    Optional<User> findUserByIdentifier(String identifier);

}