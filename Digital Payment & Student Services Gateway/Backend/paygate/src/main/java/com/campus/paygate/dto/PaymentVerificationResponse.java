package com.campus.paygate.dto;

import lombok.Data;

@Data
public class PaymentVerificationResponse {
    private boolean status;
    private String message;
    private VerificationData data;

    @Data
    public static class VerificationData {
        private String status; // Usually "success", "failed", or "abandoned"
        private String reference;
        private Long amount;
        private String gateway_response;
        private String channel;
        private String currency;
    }
}