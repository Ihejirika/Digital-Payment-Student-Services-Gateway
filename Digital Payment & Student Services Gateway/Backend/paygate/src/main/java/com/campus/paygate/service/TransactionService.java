package com.campus.paygate.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.campus.paygate.model.Transaction;
import com.campus.paygate.repository.TransactionRepository;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // Fetch by Matric Number
    public List<Transaction> getStudentTransactions(String matricNo) {
        return transactionRepository.findByMatricNo(matricNo);
    }

    // Fetch ALL transactions (Useful for admin dashboards or testing)
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}
