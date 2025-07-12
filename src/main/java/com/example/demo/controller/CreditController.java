package com.example.demo.controller;

import com.example.demo.dto.CreditFormRequest;
import com.example.demo.entity.Client;
import com.example.demo.entity.CreditForm;
import com.example.demo.repository.ClientRepository;
import com.example.demo.repository.CreditFormRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")  // Базовый путь для всех эндпоинтов
public class CreditController {

    @Autowired
    private ClientRepository clientRepo;

    @Autowired
    private CreditFormRepository formRepo;

    // Эндпоинт для создания заявки
    @PostMapping("/forms")
    public ResponseEntity<Map<String, Object>> createForm(
            @RequestBody CreditFormRequest request) throws JsonProcessingException {

        // 1. Ищем клиента по паспорту (вместо clientId)
        Client client = clientRepo.findByPassportNumber(request.getPassportNumber())
                .orElse(null);

        if (client == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "REJECTED",
                    "errors", List.of("Клиент не найден")
            ));
        }

        // 2. Парсим JSON клиента
        ObjectMapper mapper = new ObjectMapper();
        JsonNode clientData = mapper.readTree(client.getData());

        // 3. Проверяем условия
        List<String> errors = new ArrayList<>();
        LocalDate birthDate = LocalDate.parse(clientData.get("birthDate").asText());
        LocalDate employmentStart = LocalDate.parse(clientData.get("employmentStartDate").asText());
        double clientIncome = clientData.get("income").asDouble();

        // 3.1. Возраст ≥ 18 лет
        if (Period.between(birthDate, LocalDate.now()).getYears() < 18) {
            errors.add("Клиент младше 18 лет");
        }

        // 3.2. Стаж работы ≥ 5 лет
        if (Period.between(employmentStart, LocalDate.now()).getYears() <2) {
            errors.add("Стаж работы менее 5 лет");
        }

        // 3.3. Доход ≥ 150% от ежемесячного платежа
        double monthlyPayment = calculateMonthlyPayment(request.getAmount(), request.getTerm(), 0.25);
        if (request.getDeclaredIncome() < monthlyPayment * 1.5) {
            errors.add("Доход слишком низкий для запрошенной суммы");
        }

        // 3.4. Первоначальный взнос ≥ 15%
        if (request.getInitialPayment() < request.getAmount() * 0.5) {
            errors.add("Первоначальный взнос менее 5%");
        }

        // 4. Если есть ошибки — отклоняем
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "REJECTED",
                    "errors", errors
            ));
        }

        // 5. Если всё ок — одобряем и считаем условия
        Map<String, Object> response = new HashMap<>();
        response.put("status", "APPROVED");
        response.put("interestRate", 0.25);  // Ставка 25%
        response.put("monthlyPayment", monthlyPayment);
        response.put("totalOverpayment", calculateTotalOverpayment(request.getAmount(), monthlyPayment, request.getTerm()));

        // 6. Сохраняем анкету
        String formJson = mapper.writeValueAsString(Map.of(
                "amount", request.getAmount(),
                "term", request.getTerm(),
                "initialPayment", request.getInitialPayment(),
                "declaredIncome", request.getDeclaredIncome(),
                "status", "APPROVED"
        ));
        CreditForm form = new CreditForm(client, formJson);
        formRepo.save(form);

        return ResponseEntity.ok(response);
    }

    // Расчёт ежемесячного платежа (аннуитетный)
    private double calculateMonthlyPayment(double amount, int term, double annualRate) {
        double monthlyRate = annualRate / 12;
        return amount * monthlyRate * Math.pow(1 + monthlyRate, term) /
                (Math.pow(1 + monthlyRate, term) - 1);
    }

    // Общая переплата
    private double calculateTotalOverpayment(double amount, double monthlyPayment, int term) {
        return monthlyPayment * term - amount;
    }
}