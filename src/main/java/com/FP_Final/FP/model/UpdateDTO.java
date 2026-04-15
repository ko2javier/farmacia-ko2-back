package com.FP_Final.FP.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos para actualizar un artículo existente")
public class UpdateDTO {

	@Schema(example = "3", description = "Unidades vendidas a descontar del stock")
	private int cantidadVendida;

	@Schema(example = "ART-00123", description = "Código interno del artículo a actualizar")
	private String codigo;

	@Schema(example = "45", description = "Nueva cantidad en stock")
	private int cantidad;

	@Schema(example = "13.50", description = "Nuevo precio de venta (€)")
	private double precio;

	@Schema(example = "Ibuprofeno 600mg Kern", description = "Nuevo nombre del artículo")
	private String nombre;

	@Schema(example = "Antiinflamatorio", description = "Nueva categoría terapéutica")
	private String categoria;

	public String getNombre() { return nombre; }
	public void setNombre(String nombre) { this.nombre = nombre; }
	public String getCategoria() { return categoria; }
	public void setCategoria(String categoria) { this.categoria = categoria; }
	public String getCodigo() { return codigo; }
	public void setCodigo(String codigo) { this.codigo = codigo; }
	public int getCantidad() { return cantidad; }
	public void setCantidad(int cantidad) { this.cantidad = cantidad; }
	public double getPrecio() { return precio; }
	public void setPrecio(double precio) { this.precio = precio; }
	public int getCantidadVendida() { return cantidadVendida; }
	public void setCantidadVendida(int cantidadVendida) { this.cantidadVendida = cantidadVendida; }
}
