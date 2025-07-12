
package com.example.demo.dto;

import java.time.LocalDate;

public class CreateClientRequest {
    private String fullName;
    private String passportNumber;
    private LocalDate birthDate;
    private Double income; 
    private LocalDate employmentStartDate; 
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getPassportNumber() { return passportNumber; }
    public void setPassportNumber(String passportNumber) { this.passportNumber = passportNumber; }
    
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    
    public Double getIncome() { return income; }
    public void setIncome(Double income) { this.income = income; }

    public LocalDate getEmplymentStartDate() { return employmentStartDate; }
    public void setEmploymentStartDate(LocalDate employmentStartDate) { this.employmentStartDate = employmentStartDate; }
}