package com.example.demo.repository;

import com.example.demo.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    @Query(value = "SELECT EXISTS(SELECT 1 FROM clients WHERE data->>'passportNumber' = :passportNumber)",
            nativeQuery = true)
    boolean existsByPassportNumber(@Param("passportNumber") String passportNumber);
    @Query(value = "SELECT * FROM clients WHERE data->>'passportNumber' = :passportNumber LIMIT 1",
            nativeQuery = true)
    Optional<Client> findByPassportNumber(@Param("passportNumber") String passportNumber);
    @Query(value = "SELECT id FROM clients WHERE data->>'passportNumber' = :passportNumber LIMIT 1",
            nativeQuery = true)
    Optional<Long> findIdByPassportNumber(@Param("passportNumber") String passportNumber);
}