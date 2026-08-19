package com.campus.paygate.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.campus.paygate.dto.PaymentInitializationRequest;
import com.campus.paygate.dto.PaymentVerificationResponse;
import com.campus.paygate.dto.PaystackResponse;
import com.campus.paygate.model.Transaction;
import com.campus.paygate.service.PaystackService;
import com.campus.paygate.service.TransactionService;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaystackService paystackService;
    private final TransactionService transactionService; // Inject new service

    public PaymentController(PaystackService paystackService, TransactionService transactionService) {
        this.paystackService = paystackService;
        this.transactionService = transactionService;
    }

    @PostMapping("/initialize")
    public ResponseEntity<PaystackResponse> initializePayment(@RequestBody PaymentInitializationRequest request) {
        System.out.println(">>> CONTROLLER HIT! Student Email: " + request.getEmail());
        PaystackResponse response = paystackService.initializePayment(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify/{reference}")
    public ResponseEntity<PaymentVerificationResponse> verifyPayment(@PathVariable String reference) {
        PaymentVerificationResponse response = paystackService.verifyPayment(reference);
        return ResponseEntity.ok(response);
    }

    // NEW: Get history for a specific student
    @GetMapping("/history/{matricNo}") 
    public ResponseEntity<List<Transaction>> getStudentHistory(@PathVariable String matricNo) {
        List<Transaction> history = transactionService.getStudentTransactions(matricNo);
        return ResponseEntity.ok(history);
    }

    // NEW: Get all history (Good for testing right now)
    @GetMapping("/history/all")
    public ResponseEntity<List<Transaction>> getAllHistory() {
        List<Transaction> history = transactionService.getAllTransactions();
        return ResponseEntity.ok(history);
    }



    // NEW: Paystack Webhook Listener
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "x-paystack-signature", required = false) String signature) {

        // 1. Check if signature exists and is valid
        if (signature == null || !paystackService.verifyWebhookSignature(payload, signature)) {
            // If the signature is fake or missing, block the request
            return ResponseEntity.status(401).body("Unauthorized"); 
        }

        // 2. Process the event
        paystackService.processWebhookEvent(payload);

        // 3. Always return 200 OK immediately so Paystack knows you received it
        return ResponseEntity.ok().build(); 
    }
}