package com.example.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = true)
    private String userId; // Usuario que realiza la transacción

    @Column(nullable = false)
    private String vitamina; // Vitamina elegida (A/B/C/D)

    @Column(nullable = false)
    private String estado; // PENDIENTE, CONFIRMADO, RECHAZADO

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion; // Cuando se creó la transacción

    @Column(name = "fecha_confirmacion")
    private LocalDateTime fechaConfirmacion; // Cuando el usuario confirma vitamina

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago; // Cuando sube el comprobante

    @Column(name = "payment_image", columnDefinition = "TEXT")
    private String paymentImage; // Imagen base64 del comprobante

    @Column(name = "admin_validado")
    private Boolean adminValidado; // Si el admin validó la transacción

    @Column(columnDefinition = "TEXT")
    private String notas; // Información extra opcional

    @Column
    private Double monto; // Monto de la compra (opcional)

    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getVitamina() { return vitamina; }
    public void setVitamina(String vitamina) { this.vitamina = vitamina; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaConfirmacion() { return fechaConfirmacion; }
    public void setFechaConfirmacion(LocalDateTime fechaConfirmacion) { this.fechaConfirmacion = fechaConfirmacion; }

    public LocalDateTime getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }

    public String getPaymentImage() { return paymentImage; }
    public void setPaymentImage(String paymentImage) { this.paymentImage = paymentImage; }

    public Boolean getAdminValidado() { return adminValidado; }
    public void setAdminValidado(Boolean adminValidado) { this.adminValidado = adminValidado; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }
}
