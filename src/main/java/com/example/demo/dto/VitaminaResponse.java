package com.example.demo.dto;

public class VitaminaResponse {
    private int id;
    private String vitamina;

    public VitaminaResponse(int id, String vitamina) {
        this.id = id;
        this.vitamina = vitamina;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVitamina() {
        return vitamina;
    }

    public void setVitamina(String vitamina) {
        this.vitamina = vitamina;
    }
}
