package com.example.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // Unique identifier with auto-increment

    @Column(nullable = false)
    private String dato; // Data to be processed by Arduino

    @Column(nullable = false)
    private String estado; // Status of the transaction

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion; // Command sent time

    @Column(name = "fecha_procesado")
    private LocalDateTime fechaProcesado; // Command executed time (optional)

    @Column(columnDefinition = "TEXT")
    private String notas; // Extra information (optional)

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDato() {
        return dato;
    }

    public void setDato(String dato) {
        this.dato = dato;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaProcesado() {
        return fechaProcesado;
    }

    public void setFechaProcesado(LocalDateTime fechaProcesado) {
        this.fechaProcesado = fechaProcesado;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }
}
