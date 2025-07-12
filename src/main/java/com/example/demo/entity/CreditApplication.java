package com.example.demo.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "credit_applications")
public class CreditApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double amount;  // Сумма кредита

    @Column(nullable = false)
    private int term;  // Срок в месяцах

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private LocalDate birthDate;

    @ManyToOne  // Многие заявки → одному клиенту
    @JoinColumn(name = "client_id", nullable = false)  // Внешний ключ
    private Client client;  // Ссылка на клиента

    // Конструкторы
    public CreditApplication() {
    }

    public CreditApplication(double amount, int term, String status, Client client, LocalDate birthDate, String fullName) {
        this.amount = amount;
        this.term = term;
        this.status = status;
        this.client = client;
        this.birthDate = birthDate;
        this.fullName = fullName;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}