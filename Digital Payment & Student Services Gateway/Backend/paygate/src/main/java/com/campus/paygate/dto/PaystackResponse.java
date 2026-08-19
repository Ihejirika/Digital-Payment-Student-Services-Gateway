package com.campus.paygate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaystackResponse {
    private boolean status;
    private String message;
    private Data data;

    // Getters and Setters for the main class
    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Data getData() { return data; }
    public void setData(Data data) { this.data = data; }

    // Nested Data Class
    public static class Data {
        @JsonProperty("authorization_url")
        private String authorization_url;
        
        @JsonProperty("access_code")
        private String access_code;
        
        private String reference;

        // Getters and Setters for the nested class
        public String getAuthorization_url() { return authorization_url; }
        public void setAuthorization_url(String authorization_url) { this.authorization_url = authorization_url; }

        public String getAccess_code() { return access_code; }
        public void setAccess_code(String access_code) { this.access_code = access_code; }

        public String getReference() { return reference; }
        public void setReference(String reference) { this.reference = reference; }
    }
}