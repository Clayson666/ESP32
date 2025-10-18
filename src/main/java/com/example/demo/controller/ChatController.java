package com.example.demo.controller;

import com.example.demo.model.ChatMessage;
import com.example.demo.model.Transaction;
import com.example.demo.service.ChatService;
import com.example.demo.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class ChatController {

    private final TransactionService transactionService;

    private ChatService chatService;

    @Autowired
    public ChatController(TransactionService transactionService, ChatService chatService) {
        this.transactionService = transactionService;
        this.chatService = chatService;
    }

    @GetMapping
    public String showChatPage(Model model) {
        model.addAttribute("chatMessage", new ChatMessage());
        model.addAttribute("messages", new ArrayList<ChatMessage>());
        return "chat";
    }

    @PostMapping("/send")
    @ResponseBody
    public ChatMessage sendMessage(@RequestParam String message) {
        // Get response from the chat service
        String response = chatService.getChatResponse(message);
        
        // Create and return the assistant's response
        ChatMessage assistantMessage = new ChatMessage();
        assistantMessage.setRole("assistant");
        assistantMessage.setContent(response);
        
        return assistantMessage;
    }

    @GetMapping("/create")
@ResponseBody
public Transaction createPendingTransaction(@RequestParam String dato) {
    Transaction transaction = new Transaction();
    transaction.setDato(dato);
    transaction.setEstado("pendiente");
    transaction.setFechaCreacion(LocalDateTime.now());
    return transactionService.saveTransaction(transaction);
}



@GetMapping("/first-pending")
@ResponseBody
public Transaction getFirstPendingTransaction() {
    return transactionService.getFirstPendingTransaction();
}
}
