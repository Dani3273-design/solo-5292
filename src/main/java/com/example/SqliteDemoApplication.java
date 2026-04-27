package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SqliteDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SqliteDemoApplication.class, args);
        System.out.println("==============================================");
        System.out.println("  Spring Boot 3 + SQLite Demo 启动成功!");
        System.out.println("==============================================");
        System.out.println("API 端点:");
        System.out.println("  学生管理: http://localhost:8080/api/students");
        System.out.println("  成绩管理: http://localhost:8080/api/scores");
        System.out.println("==============================================");
    }
}
