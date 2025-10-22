package com.example.demo.repository;

import com.example.demo.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    // 🔹 Para buscar la primera transacción pendiente (sin validar)
    Optional<Transaction> findFirstByEstadoOrderByFechaCreacionAsc(String estado);

    // 🔹 Para buscar la primera pendiente y ya validada por admin (uso del ESP32)
    Optional<Transaction> findFirstByEstadoAndAdminValidadoTrueOrderByFechaCreacionAsc(String estado);
}