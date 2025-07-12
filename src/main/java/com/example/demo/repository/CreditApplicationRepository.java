package com.example.demo.repository;

import com.example.demo.entity.CreditApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditApplicationRepository extends JpaRepository<CreditApplication, Long> {
}