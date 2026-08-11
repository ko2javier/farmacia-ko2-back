package com.FP_Final.FP.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.FP_Final.FP.model.*;
import com.FP_Final.FP.repository.VentaCanceladaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.FP_Final.FP.repository.ArticulosRepository;
import com.FP_Final.FP.repository.VentasRepository;

@Service
public class VentaService {

    @Autowired
    private VentasRepository ventasRepository;

    @Autowired
    private ArticulosRepository articulosRepository;

    @Autowired
    private VentaCanceladaRepository canceladaRepository;

    // Obtiene el username del usuario autenticado desde el contexto de seguridad
    public String obtenerUsuarioAutenticado() {
        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (principal instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
            return userDetails.getUsername();
        }
        return principal.toString();
    }

    // Devuelve todas las ventas registradas en el sistema
    public List<Ventas> getAll() {
        return ventasRepository.findAll();
    }

    // Devuelve las ventas de un usuario concreto (para el historial personal)
    public List<Ventas> getAllByUsername(String username) {
        return ventasRepository.findByusername(username);
    }

    // Registra una única venta con fecha y hora actuales
    public Ventas registrarVenta(VentaDTO ventaDTO) {
        String username = obtenerUsuarioAutenticado();
        Ventas nuevaVenta = new Ventas(
                LocalDate.now(), LocalTime.now(), username,
                ventaDTO.getDnicliente(), ventaDTO.getNameproducto(),
                ventaDTO.getCantidad(), ventaDTO.getImporte(), ventaDTO.getCodigo()
        );
        return ventasRepository.save(nuevaVenta);
    }

    // Registra múltiples ventas en lote (un ticket con varios productos)
    public List<Ventas> registrarVentas(List<VentaDTO> ventasDTO) {
        String username = obtenerUsuarioAutenticado();
        List<Ventas> nuevasVentas = new ArrayList<>();
        for (VentaDTO dto : ventasDTO) {
            nuevasVentas.add(ventasRepository.save(
                    new Ventas(
                            LocalDate.now(),
                            LocalTime.now(),
                            username,
                            dto.getDnicliente(),
                            dto.getNameproducto(),
                            dto.getCantidad(),
                            dto.getImporte(),
                            dto.getCodigo()
                    )
            ));
        }
        return nuevasVentas;
    }

    // Cancela una venta: la mueve al historial de cancelaciones y la borra de ventas
    @Transactional
    public void cancelarVenta(Integer idVenta, String usernameResponsable) {
        // 1. Buscamos la venta original
        Ventas ventaOriginal = ventasRepository.findById(idVenta)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        // 2. Copiamos los datos de la venta al registro de cancelación
        VentaCancelada cancelacion = new VentaCancelada();
        cancelacion.setNombreProducto(ventaOriginal.getNameproducto());
        cancelacion.setCantidad(ventaOriginal.getCantidad());
        cancelacion.setImporte(ventaOriginal.getImporte());
        cancelacion.setResponsable(usernameResponsable);
        cancelacion.setFecha(LocalDateTime.now());
        cancelacion.setStatus(CancellationStatus.CANCELLED);

        // 3. Guardamos en el historial de cancelaciones
        canceladaRepository.save(cancelacion);

        // 4. Eliminamos la venta de la tabla principal
        ventasRepository.delete(ventaOriginal);
    }
}
