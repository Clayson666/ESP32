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

    public String getChatResponse(String userMessage) {
        String url = chatConfig.getChatApiUrl();
        
        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(chatConfig.getApiKey());
        // Optional OpenRouter headers
        if (chatConfig.getHttpReferer() != null && !chatConfig.getHttpReferer().isEmpty()) {
            headers.add("HTTP-Referer", chatConfig.getHttpReferer());
        }
        if (chatConfig.getTitle() != null && !chatConfig.getTitle().isEmpty()) {
            headers.add("X-Title", chatConfig.getTitle());
        }
        
        // Prepare messages
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "user", "content", userMessage));
        
        // Prepare request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", chatConfig.getModel());
        requestBody.put("messages", messages);
        
        // Create HTTP entity
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        
        try {
            // Make the API call
            ResponseEntity<Map> response = restTemplate.exchange(
                url, 
                HttpMethod.POST, 
                entity, 
                Map.class
            );
            
            // Extract and return the response
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return (String) message.get("content");
                }
            }
            return "Lo siento, no pude obtener una respuesta del asistente.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al comunicarse con el servicio de chat: " + e.getMessage();
        }
    }
}

