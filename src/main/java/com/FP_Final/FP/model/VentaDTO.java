package com.FP_Final.FP.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos para registrar una nueva venta")
public class VentaDTO {

	@Schema(example = "12345678A", description = "DNI del cliente")
	private String dnicliente;

	@Schema(example = "Ibuprofeno 600mg", description = "Nombre del producto vendido")
	private String nameproducto;

	@Schema(example = "2", description = "Cantidad de unidades vendidas")
	private int cantidad;

	@Schema(example = "25.90", description = "Importe total de la venta (€)")
	private double importe;

	@Schema(example = "ART-00123", description = "Código interno del artículo")
	private String codigo;

	public VentaDTO() {}

	public String getCodigo() { return codigo; }
	public void setCodigo(String codigo) { this.codigo = codigo; }
	public String getDnicliente() { return dnicliente; }
	public void setDnicliente(String dnicliente) { this.dnicliente = dnicliente; }
	public String getNameproducto() { return nameproducto; }
	public void setNameproducto(String nameproducto) { this.nameproducto = nameproducto; }
	public int getCantidad() { return cantidad; }
	public void setCantidad(int cantidad) { this.cantidad = cantidad; }
	public double getImporte() { return importe; }
	public void setImporte(double importe) { this.importe = importe; }
}
