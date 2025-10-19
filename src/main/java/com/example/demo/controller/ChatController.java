package com.example.demo.controller;

import com.example.demo.model.ChatMessage;
import com.example.demo.model.Transaction;
import com.example.demo.service.ChatService;
import com.example.demo.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:5500")
public class ChatController {

    private final TransactionService transactionService;
    private final ChatService chatService;

    @Autowired
    public ChatController(TransactionService transactionService, ChatService chatService) {
        this.transactionService = transactionService;
        this.chatService = chatService;
    }

    @PostMapping("/send")
    public ChatMessage sendMessage(@RequestBody ChatMessage userMessage) {
        System.out.println("[LOG] Mensaje recibido: " + userMessage.getContent());
        String content = userMessage.getContent().trim();

        ChatMessage assistantMessage = new ChatMessage();
        assistantMessage.setRole("eres un nutricionista con autoridad para decidir que vimitaminas dedbe tomar una persona de idioma español con respuestas cortas y puntuales si hay expandirse en la información se hace sino mejor ser conciso");

        // 1️⃣ Usuario envía base64 de imagen del comprobante
        if(content.startsWith("data:image")) {
            Transaction lastPending = transactionService.getFirstPendingTransaction();
            if(lastPending != null) {
                lastPending.setPaymentImage(content);
                lastPending.setFechaPago(LocalDateTime.now());
                transactionService.saveTransaction(lastPending);
                assistantMessage.setContent("Comprobante recibido. ¡Gracias por tu compra!");
                return assistantMessage;
            } else {
                assistantMessage.setContent("No hay ninguna transacción pendiente para adjuntar este comprobante.");
                return assistantMessage;
            }
        }

        // 2️⃣ Usuario confirma la vitamina (Vitamina C, Hierro, Zinc, B12)
        if(content.equalsIgnoreCase("Vitamina C") ||
        content.equalsIgnoreCase("Hierro") ||
        content.equalsIgnoreCase("Zinc") ||
        content.equalsIgnoreCase("B12")) {

            Transaction transaction = new Transaction();
            transaction.setVitamina(content);
            transaction.setEstado("PENDIENTE");
            transaction.setFechaCreacion(LocalDateTime.now());
            transactionService.saveTransaction(transaction);

            assistantMessage.setContent(
                "Perfecto, registramos tu " + content +
                ". Ahora sube la imagen del comprobante de pago."
            );
            return assistantMessage;
        }

        // 3️⃣ Texto libre → diagnóstico / recomendación por chatService
        String response = chatService.getChatResponse(content);
        System.out.println("[LOG] Respuesta del asistente: " + response);

        // Agregar sugerencia de vitaminas **solo si el chat detecta que se puede recomendar**
        response += "\nSi deseas, puedo recomendarte una vitamina: Vitamina C, Hierro, Zinc o B12. " +
                    "Luego podrás confirmar tu elección y subir el comprobante.";

        assistantMessage.setContent(response);
        return assistantMessage;
    }

    @GetMapping("/history")
    public List<ChatMessage> getHistory() {
        List<Map<String, String>> history = chatService.getConversationHistory();
        System.out.println("[LOG] Historial actual: " + history.size() + " mensajes");

        List<ChatMessage> chatHistory = new ArrayList<>();
        for (Map<String, String> msg : history) {
            ChatMessage chatMsg = new ChatMessage();
            chatMsg.setRole(msg.get("role"));
            chatMsg.setContent(msg.get("content"));
            chatHistory.add(chatMsg);
        }
        return chatHistory;
    }

    @DeleteMapping("/history")
    public void clearHistory() {
        System.out.println("[LOG] Limpiando historial...");
        chatService.clearConversationHistory();
    }

    // ----- TRANSACTIONS -----
    @PostMapping("/transactions")
    public Transaction createPendingTransaction(@RequestBody Map<String, String> payload) {
        String vitamina = payload.get("vitamina");
        Transaction transaction = new Transaction();
        transaction.setVitamina(vitamina);
        transaction.setEstado("PENDIENTE");
        transaction.setFechaCreacion(LocalDateTime.now());
        return transactionService.saveTransaction(transaction);
    }

    @GetMapping("/transactions/first-pending")
    public Transaction getFirstPendingTransaction() {
        return transactionService.getFirstPendingTransaction();
    }

    // Listar todas las transacciones (incluye imagen base64 si existe)
    @GetMapping("/transactions")
    public List<Transaction> listTransactions() {
        return transactionService.findAll();
    }

    // Validar transacción por ID: adminValidado=true y fechaConfirmacion=now
    @PutMapping("/transactions/{id}/validate")
    public Transaction validateTransaction(@PathVariable int id) {
        Transaction tx = transactionService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transacción no encontrada"));
        tx.setAdminValidado(true);
        tx.setFechaConfirmacion(LocalDateTime.now());
        return transactionService.saveTransaction(tx);
    }
}
