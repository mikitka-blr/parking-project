package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        String dbUrl = System.getenv("DB_URL");
        if (dbUrl != null && !dbUrl.startsWith("jdbc:")) {
            System.setProperty("DB_URL", "jdbc:" + dbUrl);
        } else if (dbUrl == null) {
            System.setProperty("DB_URL", "jdbc:postgresql://localhost:5432/postgres");
        }
        
        SpringApplication.run(Main.class, args);
    }
}