package com.FP_Final.FP.model;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "ventas")
public class Ventas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto_increment
    private int id;

    @Column(nullable = false)
    private LocalDate fecha; 

    @Column(nullable = false)
    private LocalTime hora; 

    @Column(name = "id_username", nullable = false)
    private String username; 

    @Column(nullable = false, length = 20)
    private String dnicliente; 

    @Column(nullable = false, length = 100)
    private String nameproducto; 

    @Column(nullable = false)
    private int cantidad; 

    @Column(nullable = false)
    private double importe; 
    
    
    
    
    @Column(name="codigo", length=13, nullable=false)
    private String codigo;

   
    
    public Ventas() {
    }
    public Ventas(LocalDate now, LocalTime now2, String username2, String dnicliente2, String nameproducto2,
			int cantidad2, double importe2, String codigo ) {
		
		this.fecha = now;
		this.hora = now2;
		this.username = username2;
		this.dnicliente = dnicliente2;
		this.nameproducto = nameproducto2;
		this.cantidad = cantidad2;
		this.importe = importe2;
		this.codigo = codigo;
	}

	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	// Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public String getUsername() {
        return username; // Getter para username como String
    }

    public void setUsername(String username) {
        this.username = username; // Setter para username como String
    }

    public String getDnicliente() {
        return dnicliente;
    }

    public void setDnicliente(String dnicliente) {
        this.dnicliente = dnicliente;
    }

    public String getNameproducto() {
        return nameproducto;
    }

    public void setNameproducto(String nameproducto) {
        this.nameproducto = nameproducto;
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

   
}
