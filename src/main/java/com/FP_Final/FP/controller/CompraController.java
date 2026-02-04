package com.FP_Final.FP.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.FP_Final.FP.model.ComprasCliente;
import com.FP_Final.FP.model.ComprasClienteDTO;
import com.FP_Final.FP.service.CompraService;

import java.util.List;

@RestController
@RequestMapping("/compras")
public class CompraController {

    @Autowired
    private CompraService compraService;

    // Endpoint para obtener todas las compras realizadas por un cliente (por DNI)
    @GetMapping("/cliente/{dni}")
    public ResponseEntity<List<ComprasCliente>> getAllComprasByCliente(@PathVariable String dni) {
        List<ComprasCliente> compras = compraService.getAll_Cliente(dni);
        if (compras.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(compras);
    }

    // Endpoint para obtener todas las compras realizadas por un producto
    @GetMapping("/producto/{prod}")
    public ResponseEntity<List<ComprasCliente>> getAllComprasByProducto(@PathVariable String prod) {
        List<ComprasCliente> compras = compraService.getAll_producto(prod);
        if (compras.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(compras);
    }

    // Endpoint para obtener todas las compras
    @GetMapping("/all")
    public ResponseEntity<List<ComprasCliente>> getAllCompras() {
        List<ComprasCliente> compras = compraService.getAll();
        if (compras.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(compras);
    }
    
 // Endpoint para registrar "múltiples" ComprasCliente de una sola vez
    @PostMapping("/registrar/list")
    public ResponseEntity<List<ComprasCliente>> registrarComprasCliente(
            @RequestBody List<ComprasClienteDTO> comprasDTO) {
        
        List<ComprasCliente> nuevasCompras = compraService.registrarComprasCliente(comprasDTO);
        return new ResponseEntity<>(nuevasCompras, HttpStatus.CREATED);
    }
}

