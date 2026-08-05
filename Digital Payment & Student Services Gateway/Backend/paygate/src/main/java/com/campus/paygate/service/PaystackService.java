package com.campus.paygate.service;

import java.nio.charset.StandardCharsets;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.campus.paygate.dto.PaymentInitializationRequest;
import com.campus.paygate.dto.PaymentVerificationResponse;
import com.campus.paygate.dto.PaystackResponse;
import com.campus.paygate.model.Transaction;
import com.campus.paygate.repository.TransactionRepository; // Added the missing import
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PaystackService {

    @Value("${paystack.secret.key}")
    private String secretKey;

    private final RestTemplate restTemplate;
    private final TransactionRepository transactionRepository; // Declared the repository

    // Injected the repository into the constructor
    public PaystackService(RestTemplate restTemplate, TransactionRepository transactionRepository) {
        this.restTemplate = restTemplate;
        this.transactionRepository = transactionRepository;
    }

    public PaystackResponse initializePayment(PaymentInitializationRequest request) {
        String paystackUrl = "https://api.paystack.co/transaction/initialize";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(secretKey.replace("Bearer ", ""));

        HttpEntity<PaymentInitializationRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<PaystackResponse> response = restTemplate.exchange(
                paystackUrl,
                HttpMethod.POST,
                entity,
                PaystackResponse.class
        );

        return response.getBody();
    }

    public PaymentVerificationResponse verifyPayment(String reference) {
        String verifyUrl = "https://api.paystack.co/transaction/verify/" + reference;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(secretKey.replace("Bearer ", ""));

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<PaymentVerificationResponse> response = restTemplate.exchange(
                verifyUrl,
                HttpMethod.GET,
                entity,
                PaymentVerificationResponse.class
        );

        PaymentVerificationResponse responseBody = response.getBody();

        // Restored the database save logic for manual verification
        if (responseBody != null && responseBody.isStatus() && "success".equals(responseBody.getData().getStatus())) {
            if (!transactionRepository.existsByReference(reference)) {
                Transaction transaction = new Transaction();
                transaction.setReference(responseBody.getData().getReference());
                transaction.setAmount(responseBody.getData().getAmount() / 100); 
                transaction.setStatus("SUCCESS");
                
                transactionRepository.save(transaction);
            }
        }

        return responseBody;
    }
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    public boolean verifyWebhookSignature(String payload, String signature) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            return hexString.toString().equals(signature);
        } catch (Exception e) {
            return false;
        }
    }

    public void processWebhookEvent(String payload) {
        try {
            JsonNode rootNode = objectMapper.readTree(payload);
            String event = rootNode.path("event").asText();

            if ("charge.success".equals(event)) {
                JsonNode dataNode = rootNode.path("data");
                String reference = dataNode.path("reference").asText();
                long amount = dataNode.path("amount").asLong();

                if (!transactionRepository.existsByReference(reference)) {
                    Transaction transaction = new Transaction();
                    transaction.setReference(reference);
                    transaction.setAmount(amount / 100); 
                    transaction.setStatus("SUCCESS");
                    
                    transactionRepository.save(transaction);
                }
            }
        } catch (Exception e) {
            System.err.println("Error processing webhook: " + e.getMessage());
        }
    }
}