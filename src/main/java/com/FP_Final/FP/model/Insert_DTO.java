package com.FP_Final.FP.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos para insertar un nuevo artículo en el inventario")
public class Insert_DTO {

	@Schema(example = "50", description = "Unidades disponibles en stock")
	private int cantidad;

	@Schema(example = "12.95", description = "Precio de venta al público (€)")
	private double precio;

	@Schema(example = "Ibuprofeno 600mg", description = "Nombre comercial del artículo")
	private String nombre;

	@Schema(example = "Antiinflamatorio", description = "Categoría terapéutica del producto")
	private String categoria;

	@Schema(example = "67031", description = "Código AEMPS (Agencia Española de Medicamentos)")
	private String aempsCode;

	@Schema(example = "Kern Pharma", description = "Laboratorio fabricante")
	private String laboratorio;

	public int getCantidad() { return cantidad; }
	public void setCantidad(int cantidad) { this.cantidad = cantidad; }
	public double getPrecio() { return precio; }
	public void setPrecio(double precio) { this.precio = precio; }
	public String getNombre() { return nombre; }
	public void setNombre(String nombre) { this.nombre = nombre; }
	public String getCategoria() { return categoria; }
	public void setCategoria(String categoria) { this.categoria = categoria; }
	public String getAempsCode() { return aempsCode; }
	public void setAempsCode(String aempsCode) { this.aempsCode = aempsCode; }
	public String getLaboratorio() { return laboratorio; }
	public void setLaboratorio(String laboratorio) { this.laboratorio = laboratorio; }
}
