package com.FP_Final.FP.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.FP_Final.FP.model.CancellationStatus;

@Entity
@Table(name = "ventas_canceladas")
public class VentaCancelada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_producto") // Esto conecta con nombre_producto en MySQL
    private String nombreProducto;

    private int cantidad;

    private Double importe; // Usamos Double para el dinero

    private String responsable; // El usuario que canceló (admin, ko2...)

    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CancellationStatus status;

    // Constructor vacío obligatorio
    public VentaCancelada() {
        this.fecha = LocalDateTime.now(); // Pone la fecha actual automáticamente al crear
    }

    // Constructor con datos (para usarlo fácil luego)
    public VentaCancelada(String nombreProducto, int cantidad, Double importe, String responsable) {
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.importe = importe;
        this.responsable = responsable;
        this.fecha = LocalDateTime.now();
    }

    // --- GETTERS Y SETTERS (Genéralos con Alt+Insert o copia estos básicos) ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public Double getImporte() { return importe; }
    public void setImporte(Double importe) { this.importe = importe; }

    public String getResponsable() { return responsable; }
    public void setResponsable(String responsable) { this.responsable = responsable; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public CancellationStatus getStatus() { return status; }
    public void setStatus(CancellationStatus status) { this.status = status; }
}