package com.FP_Final.FP.model;

public class Insert_DTO {
	private int cantidad;
	private double precio;
	private String nombre;
	private String categoria;
	private String aempsCode;
	private String laboratorio;
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
	public String getAempsCode() {
		return aempsCode;
	}
	public void setAempsCode(String aempsCode) {
		this.aempsCode = aempsCode;
	}
	public String getLaboratorio() {
		return laboratorio;
	}
	public void setLaboratorio(String laboratorio) {
		this.laboratorio = laboratorio;
	}

}
