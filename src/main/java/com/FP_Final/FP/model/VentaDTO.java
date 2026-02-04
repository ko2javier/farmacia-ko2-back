package com.FP_Final.FP.model;

public class VentaDTO {
	
	 private String dnicliente;
	    private String nameproducto;
	    private int cantidad;
	    private double importe;
	    private String codigo; // Nuevo campo para el código del artículo
	    
	    public String getCodigo() {
			return codigo;
		}


		public void setCodigo(String codigo) {
			this.codigo = codigo;
		}


		
	    public VentaDTO() {}
	    

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
