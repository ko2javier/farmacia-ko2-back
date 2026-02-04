package com.FP_Final.FP.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.FP_Final.FP.model.ComprasCliente;
import com.FP_Final.FP.model.ComprasClienteDTO;
import com.FP_Final.FP.model.Ventas;
import com.FP_Final.FP.repository.ComprasRepository;
import com.FP_Final.FP.repository.VentasRepository;

@Service
public class CompraService {
	

	@Autowired
    private ComprasRepository compras;
	
	
	@Autowired
	private VentasRepository ventasRepository;
	
	 public List<ComprasCliente> getAll_Cliente(String dni) {
	        return compras.findByDnicliente(dni);
	    }
	 
	 public List<ComprasCliente> getAll_producto(String prod) {
	        return compras.findByProducto(prod);
	    }
	 public  List<ComprasCliente> getAll(){
		 return compras.findAll();
	 }
	 public List<ComprasCliente> registrarComprasCliente(List<ComprasClienteDTO> comprasDTO) {
	        List<ComprasCliente> nuevasCompras = new ArrayList<>();

	        for (ComprasClienteDTO dto : comprasDTO) {
	            // 1) Buscar la venta asociada
	            Ventas ventaEncontrada = ventasRepository.findById(dto.getVentaId())
	                .orElseThrow(() -> new RuntimeException("No existe venta con ID: " + dto.getVentaId()));

	            // 2) Crear ComprasCliente en UNA SOLA LÍNEA gracias a nuestro constructor
	            ComprasCliente compra = new ComprasCliente(
	                ventaEncontrada,
	                    
	                dto.getProducto(),
	                dto.getCantidad(),
	                dto.getImporte(),
	                dto.getDnicliente()
	            );

	            // 3) Guardar
	            ComprasCliente guardada = compras.save(compra);
	            nuevasCompras.add(guardada);
	        }
	        return nuevasCompras;
	    }
	 
}
