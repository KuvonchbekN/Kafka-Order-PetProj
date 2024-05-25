package com.example.paymentservice.service;

import lombok.extern.slf4j.Slf4j;
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
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = "orders", groupId = "payment-group")
    public void processOrder(String order) {
        log.info("Processing order: {}", order);
        try {
            // Simulate payment processing
            if (Math.random() > 0.5) { // Simulate a random failure
                throw new RuntimeException("Payment processing failed for order: " + order);
            }
            String paymentStatus = "Payment processed for order: " + order;
            kafkaTemplate.send("payments", paymentStatus);
        } catch (Exception e) {
            log.error("Error processing order: {}", order, e);
            kafkaTemplate.send("payment-errors", "order:" + order + ":retry:0");
        }
    }
}
