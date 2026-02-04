package com.FP_Final.FP.model;



import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "comprascliente")
public class ComprasCliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto_increment
    private int id;

    @ManyToOne 
    @JoinColumn(name = "id_ventas_usuario", referencedColumnName = "id", nullable = false)
    private Ventas venta;

    @Column(nullable = false)
    private LocalDate dia; 

    @Column(nullable = false)
    private LocalTime hora; 

    @Column(nullable = false, length = 100)
    private String producto; 

    @Column(nullable = false)
    private int cantidad; 

    @Column(nullable = false)
    private double importe; 

    @Column(nullable = false, length = 20)
    private String dnicliente; 
    
	public ComprasCliente() {}
	
	
    public ComprasCliente(Ventas venta,  String producto,
                          int cantidad, double importe, String dnicliente) {
        this.venta = venta;
        this.dia = LocalDate.now();
        this.hora = LocalTime.now();
        this.producto = producto;
        this.cantidad = cantidad;
        this.importe = importe;
        this.dnicliente = dnicliente;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Ventas getVenta() {
        return venta;
    }

    public void setVenta(Ventas venta) {
        this.venta = venta;
    }

    public LocalDate getDia() {
        return dia;
    }

    public void setDia(LocalDate dia) {
        this.dia = dia;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }

    public String getDnicliente() {
        return dnicliente;
    }

    public void setDnicliente(String dnicliente) {
        this.dnicliente = dnicliente;
    }
}
