package com.example.demo.repository;

import com.example.demo.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    // Additional query methods can be defined here

    Transaction findFirstByEstadoOrderByFechaCreacionAsc(String estado);
}
