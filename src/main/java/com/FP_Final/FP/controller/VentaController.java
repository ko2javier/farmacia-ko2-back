package com.FP_Final.FP.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.FP_Final.FP.model.VentaDTO;
import com.FP_Final.FP.model.Ventas;
import com.FP_Final.FP.service.VentaService;

import java.util.List;

@RestController
@RequestMapping("/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    // Endpoint para obtener todas las ventas
    @GetMapping("/all")
    public ResponseEntity<List<Ventas>> getAllVentas() {
        List<Ventas> ventas = ventaService.getAll();
        if (ventas.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content si no hay ventas
        }
        return ResponseEntity.ok(ventas); // 200 OK con la lista de ventas
    }

    // Endpoint para obtener todas las ventas por usuario (username)
    @GetMapping("/{username}")
    public ResponseEntity<List<Ventas>> getAllVentasByUsuario(@PathVariable String username) {
        List<Ventas> ventas = ventaService.getAll_username(username);
        if (ventas.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content si no hay ventas para ese usuario
        }
        return ResponseEntity.ok(ventas); // 200 OK con la lista de ventas para el usuario
    }
    
    @PostMapping("/registrar")
    public ResponseEntity<Ventas> registrarVenta(@RequestBody VentaDTO ventaDTO) {
        Ventas nuevaVenta = ventaService.registrarVenta(ventaDTO);
        return ResponseEntity.ok(nuevaVenta);
    }
    
    @PostMapping("/registrar/list")
    public ResponseEntity<List<Ventas>> registrarVentas(@RequestBody List<VentaDTO> ventasDTO) {
        List<Ventas> nuevasVentas = ventaService.registrarVentas(ventasDTO);
        return ResponseEntity.ok(nuevasVentas);
    }
}
