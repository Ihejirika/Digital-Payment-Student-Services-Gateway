package com.campus.paygate.repository;

import com.campus.paygate.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends MongoRepository<Transaction, String> {

    List<Transaction> findByMatricNo(String matricNo);

    Optional<Transaction> findByReference(String reference);
}