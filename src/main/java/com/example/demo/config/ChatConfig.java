package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ChatConfig {
    
    @Value("${chatbot.api.url:https://openrouter.ai/api/v1/chat/completions}")
    private String chatApiUrl;
    
    @Value("${chatbot.api.key:}")
    private String apiKey;
    
    @Value("${chatbot.model:openrouter/auto}")
    private String model;
    
    @Value("${chatbot.http_referer:}")
    private String httpReferer;
    
    @Value("${chatbot.title:CreaMas}")
    private String title;
    
    // Getters
    public String getChatApiUrl() {
        return chatApiUrl;
    }
    
    public String getApiKey() {
        return apiKey;
    }
    
    public String getModel() {
        return model;
    }
    
    public String getHttpReferer() {
        return httpReferer;
    }
    
    public String getTitle() {
        return title;
    }
    
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(15_000);
        factory.setReadTimeout(60_000);
        return new RestTemplate(factory);
    }
}
