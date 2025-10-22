package com.expensemate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ExpenseMateApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExpenseMateApplication.class, args);
        System.out.println(" ExpenseMate Application Started Successfully!");
        System.out.println(" Access Dashboard: http://localhost:8080");
        System.out.println(" Gamified Progress System Enabled");
    }
}
