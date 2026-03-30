package com.FP_Final.FP.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.FP_Final.FP.model.VentaCancelada;
import com.FP_Final.FP.repository.VentaCanceladaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.FP_Final.FP.model.Articulos;
import com.FP_Final.FP.model.VentaDTO;
import com.FP_Final.FP.model.Ventas;
import com.FP_Final.FP.repository.ArticulosRepository;
import com.FP_Final.FP.repository.VentasRepository;
//import org.springframework.security.oauth2.jwt.Jwt;

@Service
public class VentaService {
	
	@Autowired
    private VentasRepository ventas_repository;
	@Autowired
    private ArticulosRepository articulosRepository;

	@Autowired
	private VentaCanceladaRepository canceladaRepository; // El nuevo repo
	
	public String obtenerUsuarioAutenticado() {
	    Object principal = SecurityContextHolder
	            .getContext()
	            .getAuthentication()
	            .getPrincipal();

	    if (principal instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
	        return userDetails.getUsername();
	    }

	    // fallback
	    return principal.toString();
	}


	

	public List<Ventas> getAll() {
		return ventas_repository.findAll();
	}
	
	public List<Ventas> getAll_username(String id_username) {
		return ventas_repository.findByusername(id_username);
	}
	
	//Registrar venta
	public Ventas registrarVenta(VentaDTO ventaDTO) {
	    String username = obtenerUsuarioAutenticado(); //Usuario autenticado
	    System.out.println("Registrando venta para usuario: " + username);
	   // Articulos articulo = articulosRepository.findByCodigo(ventaDTO.getCodigo());
	    
	    Ventas nuevaVenta = new Ventas(
	        LocalDate.now(), LocalTime.now(), username,
	        ventaDTO.getDnicliente(), ventaDTO.getNameproducto(),
	        ventaDTO.getCantidad(), ventaDTO.getImporte(), ventaDTO.getCodigo()
	    );

	    return ventas_repository.save(nuevaVenta);
	}
	public List<Ventas> registrarVentas(List<VentaDTO> ventasDTO) {
        String username = obtenerUsuarioAutenticado(); //Usuario autenticado
        List<Ventas> nuevasVentas = new ArrayList<>();

        for (VentaDTO dto : ventasDTO) {
        	
        	
        	nuevasVentas.add(ventas_repository.save(
				new Ventas(
					LocalDate.now(),
					LocalTime.now(),
					username,            // id_username
					dto.getDnicliente(),
					dto.getNameproducto(),
					dto.getCantidad(),
					dto.getImporte(),dto.getCodigo()
					
				)));
        	
        	
            
        }

        return nuevasVentas;
    }

	@Transactional
	public void cancelarVenta(Integer idVenta, String usernameResponsable) {
		// 1. Buscamos la venta original
		Ventas ventaOriginal = ventas_repository.findById(idVenta)
				.orElseThrow(() -> new RuntimeException("Venta no encontrada"));

		// 2. Creamos el objeto de VentaCancelada copiando los datos
		VentaCancelada cancelacion = new VentaCancelada();
		cancelacion.setNombreProducto(ventaOriginal.getNameproducto()); // Ajusta los getters según tu entidad Venta
		cancelacion.setCantidad(ventaOriginal.getCantidad());
		cancelacion.setImporte(ventaOriginal.getImporte());
		cancelacion.setResponsable(usernameResponsable); // El usuario que ha dado al botón
		cancelacion.setFecha(LocalDateTime.now());

		// 3. Guardamos en el historial de cancelaciones
		canceladaRepository.save(cancelacion);

		// 4. Borramos de la tabla de ventas original
		ventas_repository.delete(ventaOriginal);
	}


}
