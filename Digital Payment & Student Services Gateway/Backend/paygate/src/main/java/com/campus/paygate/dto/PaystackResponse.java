package com.campus.paygate.dto;

import lombok.Data;

@Data
public class PaystackResponse {
    private boolean status;
    private String message;
    private ResponseData data;

    @Data
    public static class ResponseData {
        private String authorization_url;
        private String access_code;
        private String reference;
    }
}