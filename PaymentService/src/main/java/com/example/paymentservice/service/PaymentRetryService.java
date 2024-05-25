package com.example.paymentservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.example.paymentservice.service.PaymentErrorService.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentRetryService {

    private static final Logger log = LoggerFactory.getLogger(PaymentRetryService.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = "payment-retries", groupId = "retry-group")
    public void processRetry(String message) {
        log.info("Processing retry message: {}", message);

        // Parse the message to get the order details
        String orderId = extractOrderIdFromMessage(message);
        int retryCount = extractRetryCountFromMessage(message);

        try {
            // Simulate payment processing
            if (Math.random() > 0.01) { // Simulate a random failure
                throw new RuntimeException("Payment processing failed for order: " + orderId);
            }
            String paymentStatus = "Payment processed for order: " + orderId;
            kafkaTemplate.send("payments", paymentStatus);
        } catch (Exception e) {
            log.error("Error processing retry for order: {}", orderId, e);
            if (retryCount < PaymentErrorService.MAX_RETRIES) {
                String retryMessage = createRetryMessage(orderId, retryCount + 1);
                kafkaTemplate.send(PaymentErrorService.RETRY_TOPIC, retryMessage);
            } else {
                kafkaTemplate.send(PaymentErrorService.DLQ_TOPIC, message);
                notifyAdmin(orderId);
            }
        }
    }

    private String extractOrderIdFromMessage(String message) {
        // Extract the order ID from the message
        return message.split(":")[1].trim();
    }

    private int extractRetryCountFromMessage(String message) {
        // Extract the retry count from the message
        return Integer.parseInt(message.split(":")[3].trim());
    }

    private String createRetryMessage(String orderId, int retryCount) {
        // Create a retry message with the order ID and retry count
        return "order:" + orderId + ":retry:" + retryCount;
    }

    private void notifyAdmin(String orderId) {
        // Placeholder for notification logic (e.g., sending an email or SMS)
        log.info("Notifying admin about failure of order: {}", orderId);
        // Implement actual notification logic here (e.g., using an email service)
    }
}
