package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 交易系统应用程序入口类
 * 
 * @SpringBootApplication 标识这是一个 Spring Boot 应用程序
 * @EnableCaching 启用 Spring 缓存功能
 */
@SpringBootApplication
@EnableCaching
public class TransactionApplication {
    /**
     * 应用程序主入口方法
     * 启动 Spring Boot 应用程序
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(TransactionApplication.class, args);
    }
}