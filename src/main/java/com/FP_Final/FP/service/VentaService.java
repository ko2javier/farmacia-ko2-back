package com.FP_Final.FP.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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


}
