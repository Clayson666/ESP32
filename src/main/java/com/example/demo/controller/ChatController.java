package com.example.demo.controller;

import com.example.demo.dto.VitaminaResponse;
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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
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
        assistantMessage.setRole("assistant");

        // 1️⃣ Imagen del comprobante
        if (content.startsWith("data:image")) {
            Transaction lastPending = transactionService.getFirstPendingTransaction();
            System.out.println("[DEBUG] Buscando transacción pendiente...");
            if (lastPending == null) {
                System.out.println("[DEBUG] ❌ No se encontró transacción pendiente");
                assistantMessage.setContent("No hay ninguna transacción pendiente para adjuntar este comprobante.");
                return assistantMessage;
            }

            System.out.println("[DEBUG] ✅ Transacción encontrada: " + lastPending.getId());
            lastPending.setPaymentImage(content);
            lastPending.setFechaPago(LocalDateTime.now());
            transactionService.saveTransaction(lastPending);
            System.out.println("[DEBUG] ✅ Comprobante guardado correctamente");
            assistantMessage.setContent("Comprobante recibido. ¡Gracias por tu compra!");
            return assistantMessage;
        }

        // 2️⃣ Confirmación directa de vitamina (crea transacción)
        if (esVitaminaValida(content)) {
            Transaction tx = new Transaction();
            tx.setVitamina(content);
            tx.setEstado("PENDIENTE");
            tx.setFechaCreacion(LocalDateTime.now());
            transactionService.saveTransaction(tx);

            assistantMessage.setContent(
                    "Confirmado: " + content +
                            ". Sube el comprobante. Si tomas medicación o estás embarazada, consúltanos antes de iniciar.");
            return assistantMessage;
        }

        // 3️⃣ Texto libre → interacción con IA
        String promptSistema = """
                Eres un nutricionista colegiado.
                Solo puedes recomendar UNA vitamina entre: Vitamina C, Hierro, Zinc o B12.
                No menciones ninguna otra.
                Responde en español, de forma profesional y breve (máx 70 palabras).
                """;

        String inputFinal = promptSistema + "\nUsuario: " + content;

        String raw = chatService.getChatResponse(inputFinal);
        System.out.println("[LOG] Respuesta del asistente (raw): " + raw);

        // ✂️ Filtro y limpieza
        raw = filtrarVitaminasNoPermitidas(raw);
        String breve = limitarPalabras(limpiarRelleno(raw), 70);

        if (!breve.toLowerCase().startsWith("nutricionista"))
            breve = "Nutricionista: " + breve;

        assistantMessage.setContent(breve);
        return assistantMessage;
    }

    // ---------- Helpers de limpieza ----------

    private String limpiarRelleno(String t) {
        if (t == null)
            return "";
        t = t.replaceAll("(?i)como modelo de lenguaje[^.\\n]*[.\\n]?", "");
        t = t.replaceAll("(?i)no soy un sustituto[^.\\n]*[.\\n]?", "");
        t = t.replaceAll("\\s+", " ").trim();
        return t;
    }

    private String limitarPalabras(String t, int max) {
        String[] w = t.split("\\s+");
        if (w.length <= max)
            return t;
        return String.join(" ", Arrays.copyOfRange(w, 0, max)) + "...";
    }

    private String filtrarVitaminasNoPermitidas(String t) {
        String[] prohibidas = { "vitamina d", "vitamina e", "vitamina a", "omega", "calcio", "magnesio" };
        for (String p : prohibidas) {
            t = t.replaceAll("(?i)" + p, "Vitamina C");
        }
        return t;
    }

    private boolean esVitaminaValida(String s) {
        return s.equalsIgnoreCase("Vitamina C") ||
                s.equalsIgnoreCase("Hierro") ||
                s.equalsIgnoreCase("Zinc") ||
                s.equalsIgnoreCase("B12");
    }

    // ---------- Historial ----------
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

    // ---------- Transacciones ----------
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
    public VitaminaResponse getFirstPendingTransaction() {
    Transaction transaction = transactionService.getFirstPendingTransaction();
    if (transaction != null && Boolean.TRUE.equals(transaction.getAdminValidado())) {
        return new VitaminaResponse(transaction.getId(), transaction.getVitamina());
    } else {
        return new VitaminaResponse(0 , null);
    }
}
    @GetMapping("/transactions")
    public List<Transaction> listTransactions() {
        return transactionService.findAll();
    }

    @PutMapping("/transactions/{id}/validate")
    public Transaction validateTransaction(@PathVariable int id) {
        Transaction tx = transactionService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transacción no encontrada"));
        tx.setAdminValidado(true);
        tx.setFechaConfirmacion(LocalDateTime.now());
        return transactionService.saveTransaction(tx);
    }
}