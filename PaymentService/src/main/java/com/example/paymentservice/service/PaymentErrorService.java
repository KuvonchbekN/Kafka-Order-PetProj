package com.example.paymentservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentErrorService {

    private static final Logger log = LoggerFactory.getLogger(PaymentErrorService.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public static final int MAX_RETRIES = 3;
    public static final String RETRY_TOPIC = "payment-retries";
    public static final String DLQ_TOPIC = "orders.DLT"; // Dead Letter Queue Topic

    @KafkaListener(topics = "payment-errors", groupId = "error-group")
    public void handlePaymentError(String error) {
        log.info("Handling payment error: {}", error);

        // Parse the error message to extract necessary information
        String orderId = extractOrderIdFromError(error);
        int retryCount = extractRetryCountFromError(error);

        if (retryCount < MAX_RETRIES) {
            // Increment the retry count and send the message to the retry topic
            String retryMessage = createRetryMessage(orderId, retryCount + 1);
            kafkaTemplate.send(RETRY_TOPIC, retryMessage);
            log.info("Retrying order: {}, attempt: {}", orderId, retryCount + 1);
        } else {
            // Send the message to the dead-letter topic
            kafkaTemplate.send(DLQ_TOPIC, error);
            log.info("Sending order to DLQ: {}", orderId);
            notifyAdmin(orderId);
        }
    }

    private String extractOrderIdFromError(String error) {
        // Extract the order ID from the error message
        return error.split(":")[1].trim();
    }

    private int extractRetryCountFromError(String error) {
        // Extract the retry count from the error message
        String[] split = error.split(":");
        String count = split[4];
        return Integer.parseInt(count.trim());
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
