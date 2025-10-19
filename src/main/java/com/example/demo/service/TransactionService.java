package com.example.demo.service;

import com.example.demo.model.Transaction;
import com.example.demo.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction saveTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public Transaction getFirstPendingTransaction() {
        return transactionRepository.findFirstByEstadoAndAdminValidadoTrueOrderByFechaCreacionAsc("PENDIENTE");
    }

    // Listar todas las transacciones
    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    // Buscar transacción por ID
    public Optional<Transaction> findById(int id) {
        return transactionRepository.findById(id);
    }
}
