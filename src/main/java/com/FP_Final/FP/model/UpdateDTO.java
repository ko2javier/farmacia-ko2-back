package com.FP_Final.FP.model;

public class UpdateDTO {
	private int cantidadVendida;
	
	private String codigo;
	private int cantidad;
	private double precio;
	private String nombre;
	private String categoria;
	
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

	public String getCodigo() {
		return codigo;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public double getPrecio() {
		return precio;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public int getCantidadVendida() {
		return cantidadVendida;
	}

	public void setCantidadVendida(int cantidadVendida) {
		this.cantidadVendida = cantidadVendida;
	}
	

}
