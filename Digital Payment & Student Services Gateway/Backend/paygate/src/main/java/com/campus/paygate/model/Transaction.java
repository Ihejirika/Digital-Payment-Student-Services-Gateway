package com.campus.paygate.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "transactions")
public class Transaction {
    @Id
    private String id;
    
    private String matricNo; 
    private String reference;
    
    private Double amount;
    private String description;
    private String status;
    private LocalDateTime transactionDate;
}