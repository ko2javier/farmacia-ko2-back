package com.FP_Final.FP.controller;

import com.FP_Final.FP.model.VentaCancelada;
import com.FP_Final.FP.service.VentaCanceladaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cancelaciones")
@CrossOrigin(origins = "*")
public class VentaCanceladaController {

    @Autowired
    private VentaCanceladaService ventaCanceladaService;

    // GET /cancelaciones — devuelve el historial completo de ventas canceladas
    @GetMapping
    public List<VentaCancelada> listarCancelaciones() {
        return ventaCanceladaService.obtenerTodas();
    }

    // POST /cancelaciones — registra una nueva cancelación en el historial
    @PostMapping
    public VentaCancelada guardarCancelacion(@RequestBody VentaCancelada ventaCancelada) {
        return ventaCanceladaService.registrarCancelacion(ventaCancelada);
    }
}
