package com.example.inventoryservice.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {
    @KafkaListener(topics = "orders", groupId = "inventory-group")
    public void updateInventory(String order) {
        // Simulate inventory update
        System.out.println("Inventory updated for order: " + order);
    }
}
