package com.expensemate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * ExpenseMate - Smart Expense & Budget Tracker with Gamified Progress System
 * 
 * Main application class that bootstraps the Spring Boot application.
 * Enables scheduling for automated tasks like budget resets and email alerts.
 * 
 * @author ExpenseMate Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableScheduling
public class ExpenseMateApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExpenseMateApplication.class, args);
        System.out.println("🚀 ExpenseMate Application Started Successfully!");
        System.out.println("📊 Access Dashboard: http://localhost:8080");
        System.out.println("🎮 Gamified Progress System Enabled");
    }
}