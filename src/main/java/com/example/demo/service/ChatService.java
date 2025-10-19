package com.example.demo.service;

import com.example.demo.config.ChatConfig;
import com.example.demo.model.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class ChatService {

    @Autowired
    private ChatConfig chatConfig;

    @Autowired
    private RestTemplate restTemplate;

    // Historial global de la conversación (en memoria)
    private final List<Map<String, String>> conversationHistory = new ArrayList<>();

    /**
     * Envía el mensaje del usuario al servicio de chat y devuelve la respuesta.
     * Mantiene un historial completo de la conversación.
     */
    public String getChatResponse(String userMessage) {
        // Validar mensaje
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return "No se recibió ningún mensaje.";
        }

        String url = chatConfig.getChatApiUrl();

        // --- Agregar mensaje del usuario al historial ---
        Map<String, String> userMsgMap = new HashMap<>();
        userMsgMap.put("role", "user");
        userMsgMap.put("content", userMessage);
        conversationHistory.add(userMsgMap);

        // --- Headers ---
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(chatConfig.getApiKey());

        if (chatConfig.getHttpReferer() != null && !chatConfig.getHttpReferer().isEmpty()) {
            headers.add("HTTP-Referer", chatConfig.getHttpReferer());
        }
        if (chatConfig.getTitle() != null && !chatConfig.getTitle().isEmpty()) {
            headers.add("X-Title", chatConfig.getTitle());
        }

        // --- Request body con TODO el historial ---
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", chatConfig.getModel());
        requestBody.put("messages", conversationHistory);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");

                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> assistantMessage = (Map<String, Object>) choices.get(0).get("message");
                    String content = (String) assistantMessage.get("content");

                    // --- Agregar respuesta del asistente al historial ---
                    Map<String, String> assistantMsgMap = new HashMap<>();
                    assistantMsgMap.put("role", "assistant");
                    assistantMsgMap.put("content", content);
                    conversationHistory.add(assistantMsgMap);

                    return content;
                }
            }

            return "Lo siento, no pude obtener una respuesta del asistente.";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error al comunicarse con el servicio de chat: " + e.getMessage();
        }
    }

    /**
     * Devuelve una copia del historial de conversación
     */
    public List<Map<String, String>> getConversationHistory() {
        return new ArrayList<>(conversationHistory);
    }

    /**
     * Limpia todo el historial de conversación
     */
    public void clearConversationHistory() {
        conversationHistory.clear();
    }
}