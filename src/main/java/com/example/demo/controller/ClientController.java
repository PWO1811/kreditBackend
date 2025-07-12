package com.example.demo.controller;

import com.example.demo.entity.Client;
import com.example.demo.repository.ClientRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientRepository clientRepository;
    private final ObjectMapper objectMapper;

    public ClientController(ClientRepository clientRepository, ObjectMapper objectMapper) {
        this.clientRepository = clientRepository;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public ResponseEntity<?> createClient(@RequestBody String clientJson) {
        try {
            String passportNumber = extractPassportNumber(clientJson);
            if (clientRepository.existsByPassportNumber(passportNumber)) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "error", "Клиент с таким номером паспорта уже существует",
                                "passportNumber", passportNumber
                        ));
            }

            Client client = new Client(clientJson);
            client = clientRepository.save(client);

            return ResponseEntity.ok(Map.of(
                    "id", client.getId(),
                    "passportNumber", passportNumber,
                    "message", "Клиент успешно создан"
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "error", "Ошибка при создании клиента",
                            "details", e.getMessage()
                    ));
        }
    }

    @GetMapping("/find")
    public ResponseEntity<?> findClientByPassport(
            @RequestParam String passportNumber) {

        Optional<Client> client = clientRepository.findByPassportNumber(passportNumber);

        return client.map(c -> ResponseEntity.ok(Map.of(
                        "exists", true,
                        "clientId", c.getId(),
                        "clientData", c.getData(),
                        "passportNumber", passportNumber
                )))
                .orElseGet(() -> ResponseEntity.ok(Map.of(
                        "exists", false,
                        "passportNumber", passportNumber,
                        "message", "Клиент не найден"
                )));
    }

    @GetMapping("/find-id")
    public ResponseEntity<?> findClientIdByPassport(
            @RequestParam String passportNumber) {

        Optional<Long> clientId = clientRepository.findIdByPassportNumber(passportNumber);

        return clientId.map(id -> ResponseEntity.ok(Map.of(
                        "clientId", id,
                        "passportNumber", passportNumber
                )))
                .orElseGet(() -> ResponseEntity.status(404).body(
                        Map.of(
                                "error", "Клиент с паспортом " + passportNumber + " не найден",
                                "passportNumber", passportNumber
                        )
                ));
    }

    private String extractPassportNumber(String json) throws JsonProcessingException {
        JsonNode root = objectMapper.readTree(json);
        JsonNode passportNode = root.path("passportNumber");
        if (passportNode.isMissingNode()) {
            throw new IllegalArgumentException("Отсутствует обязательное поле 'passportNumber'");
        }
        return passportNode.asText();
    }
}