package com.example.paymentservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
public class DeadLetterService {

    @KafkaListener(topics = "orders.DLT", groupId = "dlt-group")
    public void processDeadLetter(String message) {
        System.out.println("Processing dead letter message: " + message);

        // Parse the message to extract necessary information
        String orderId = extractOrderIdFromMessage(message);
        int retryCount = extractRetryCountFromMessage(message);

        // Log the dead-letter message
        logDeadLetterMessage(orderId, message);

        // Notify the administrator
        notifyAdmin(orderId, message);

        // Optionally, you could also save the message to a database or another persistent store for further analysis
    }

    private String extractOrderIdFromMessage(String message) {
        // Extract the order ID from the message
        // This is a placeholder implementation; adjust as per your message format
        return message.split(":")[1].trim();
    }

    private int extractRetryCountFromMessage(String message) {
        // Extract the retry count from the message
        // This is a placeholder implementation; adjust as per your message format
        return Integer.parseInt(message.split(":")[3].trim());
    }

    private void logDeadLetterMessage(String orderId, String message) {
        // Log the dead-letter message
        System.out.println("Logging dead-letter message for order ID: " + orderId);
        System.out.println("Dead-letter message: " + message);
        // Implement actual logging logic here (e.g., using a logging framework)
    }

    private void notifyAdmin(String orderId, String message) {
        // Placeholder for notification logic (e.g., sending an email or SMS)
        System.out.println("Notifying admin about dead-letter message for order ID: " + orderId);
        System.out.println("Dead-letter message: " + message);
        // Implement actual notification logic here (e.g., using an email service)
    }
}

