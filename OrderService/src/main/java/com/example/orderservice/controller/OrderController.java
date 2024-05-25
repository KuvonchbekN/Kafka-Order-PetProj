package com.example.orderservice.controller;

import com.example.orderservice.service.OrderProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderProducer orderProducer;

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody String order) {
        orderProducer.sendOrder(order);
        return ResponseEntity.ok("Order sent to Kafka topic");
    }

    @GetMapping
    public ResponseEntity<String> greet(){
        return ResponseEntity.ok("Greetings from Kafka topic");
    }
}
