package com.example.demo.repository;

import com.example.demo.entity.CreditForm;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CreditFormRepository extends JpaRepository<CreditForm, Long> {
    List<CreditForm> findByClientId(Long clientId);  // Найти все анкеты клиента
}