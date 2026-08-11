package com.FP_Final.FP.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.FP_Final.FP.model.VentaDTO;
import com.FP_Final.FP.model.Ventas;
import com.FP_Final.FP.service.ActivityLogService;
import com.FP_Final.FP.service.VentaService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@RestController
@RequestMapping("/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    private HttpServletRequest request;

    // GET /ventas/all — devuelve todas las ventas del sistema (solo SUPERADMIN/ADMIN)
    @GetMapping("/all")
    public ResponseEntity<List<Ventas>> obtenerTodasLasVentas() {
        List<Ventas> ventas = ventaService.getAll();
        if (ventas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(ventas);
    }

    // GET /ventas/{username} — devuelve el historial de ventas de un usuario concreto
    @GetMapping("/{username}")
    public ResponseEntity<List<Ventas>> obtenerVentasPorUsuario(@PathVariable String username) {
        List<Ventas> ventas = ventaService.getAllByUsername(username);
        if (ventas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(ventas);
    }

    // POST /ventas/registrar — registra una única venta
    @PostMapping("/registrar")
    public ResponseEntity<Ventas> registrarVenta(@RequestBody VentaDTO ventaDTO) {
        Ventas nuevaVenta = ventaService.registrarVenta(ventaDTO);
        return ResponseEntity.ok(nuevaVenta);
    }

    // POST /ventas/registrar/list — registra un ticket completo (varios productos de una vez)
    @PostMapping("/registrar/list")
    public ResponseEntity<List<Ventas>> registrarVentas(@RequestBody List<VentaDTO> ventasDTO) {
        List<Ventas> nuevasVentas = ventaService.registrarVentas(ventasDTO);
        return ResponseEntity.ok(nuevasVentas);
    }

    // DELETE /ventas/cancelar/{id} — cancela una venta y la mueve al historial de cancelaciones
    @DeleteMapping("/cancelar/{id}")
    public ResponseEntity<Void> cancelarVenta(@PathVariable Integer id, @RequestParam String responsable) {
        ventaService.cancelarVenta(id, responsable);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        activityLogService.log(username, "CANCEL_SALE", "id=" + id, request.getRemoteAddr());
        return ResponseEntity.ok().build();
    }
}
