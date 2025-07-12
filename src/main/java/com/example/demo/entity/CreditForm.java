package com.example.demo.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "forms")
public class CreditForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "form_id")
    private Long formId;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private String data;  // JSON-строка с данными анкеты

    // Конструкторы
    public CreditForm() {}

    public CreditForm(Client client, String data) {
        this.client = client;
        this.data = data;
    }

    // Геттеры и сеттеры
    public Long getFormId() {
        return formId;
    }

    public Client getClient() {
        return client;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}