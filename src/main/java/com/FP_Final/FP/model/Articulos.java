package com.FP_Final.FP.model;


import jakarta.persistence.*;

@Entity
@Table(name = "articulos")
public class Articulos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto_increment
    private int id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 50)
    private String categoria;
    
    public Articulos() {}

    public Articulos(String nombre, String categoria, double precio, int cantidad, String codigo) {
		this.nombre = nombre;
		this.categoria = categoria;
		this.precio = precio;
		this.cantidad = cantidad;
		this.codigo = codigo;
	}

	@Column(nullable = false)
    private double precio;

    @Column(nullable = false)
    private int cantidad;
    

    @Column(nullable = false, length = 13, unique = true)
    private String codigo;

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public double getPrecio() {
		return precio;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}
    
    
}