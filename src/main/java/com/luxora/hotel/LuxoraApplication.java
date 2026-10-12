package com.luxora.hotel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LuxoraApplication {

    public static void main(String[] args) {
        SpringApplication.run(LuxoraApplication.class, args);
        System.out.println("\n========================================================");
        System.out.println("   LUXORA Smart Hotel Management System is Running!");
        System.out.println("   Access Web Application at: http://localhost:8080");
        System.out.println("========================================================\n");
    }
}
