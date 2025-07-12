package com.example.demo.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;

@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private String data;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    // Обязательный конструктор без аргументов
    public Client() {
    }

    // Конструктор с параметром
    public Client(String data) {
        this.data = data;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    // Метод для получения passportNumber из JSON
    public String getPassportNumber() {
        try {
            JsonNode root = new ObjectMapper().readTree(data);
            return root.path("passportNumber").asText();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse client data", e);
        }
    }
}