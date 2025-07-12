package com.example.demo.dto;

public class CreditFormRequest {
    private String passportNumber;
    private double amount;
    private int term;
    private double initialPayment;
    private double declaredIncome;
    private int employmentYears;

    // Геттеры и сеттеры (обязательно!)
    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public double getInitialPayment() {
        return initialPayment;
    }

    public void setInitialPayment(double initialPayment) {
        this.initialPayment = initialPayment;
    }

    public double getDeclaredIncome() {
        return declaredIncome;
    }

    public void setDeclaredIncome(double declaredIncome) {
        this.declaredIncome = declaredIncome;
    }

    public int getEmploymentYears() {
        return employmentYears;
    }

    public void setEmploymentYears(int employmentYears) {
        this.employmentYears = employmentYears;
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
}