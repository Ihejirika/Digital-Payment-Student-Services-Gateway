package com.campus.paygate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.campus.paygate.model.Transaction;

public interface TransactionRepository extends MongoRepository<Transaction, String> {

    List<Transaction> findByMatricNo(String matricNo);

    Optional<Transaction> findByReference(String reference);

    boolean existsByReference(String reference);

}