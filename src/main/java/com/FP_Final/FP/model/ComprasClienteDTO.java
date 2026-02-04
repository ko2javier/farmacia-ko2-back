package com.FP_Final.FP.model;

public class ComprasClienteDTO {

    // Este campo es clave para relacionar con la venta existente:
    private int ventaId;
    
    // Datos de la compra
    private String dnicliente;
    private String producto;
    private int cantidad;
    private double importe;

   
    // Getters y Setters
    public int getVentaId() {
        return ventaId;
    }
    public void setVentaId(int ventaId) {
        this.ventaId = ventaId;
    }

    public String getDnicliente() {
        return dnicliente;
    }
    public void setDnicliente(String dnicliente) {
        this.dnicliente = dnicliente;
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
}

