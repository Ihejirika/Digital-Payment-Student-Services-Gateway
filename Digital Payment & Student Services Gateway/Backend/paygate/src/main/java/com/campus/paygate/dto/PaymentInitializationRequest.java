package com.campus.paygate.dto;

import lombok.Data;

@Data
public class PaymentInitializationRequest {
    private String email;
    // Note: Paystack expects the amount in Kobo (Naira * 100)
    private String amount; 
    private String reference;
}