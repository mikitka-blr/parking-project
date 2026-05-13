package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        String dbUrl = System.getenv("DB_URL");
        if (dbUrl == null) dbUrl = System.getenv("DATABASE_URL");

        if (dbUrl != null) {
            // Очищаем от случайных кавычек или HTML тегов при копировании
            dbUrl = dbUrl.replace("\"", "").replace("'", "").replaceAll("<[^>]*>", "").trim();
            if (!dbUrl.startsWith("jdbc:")) {
                dbUrl = "jdbc:" + dbUrl;
            }
            System.setProperty("spring.datasource.url", dbUrl);
        }
        
        SpringApplication.run(Main.class, args);
    }
}