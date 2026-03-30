package com.FP_Final.FP.controller;

import com.FP_Final.FP.model.VentaCancelada;
import com.FP_Final.FP.service.VentaCanceladaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cancelaciones") // Ruta base simplificada
@CrossOrigin(origins = "*")
public class VentaCanceladaController {

    @Autowired
    private VentaCanceladaService service;

    // GET: Dame la lista (No necesita argumentos)
    @GetMapping
    public List<VentaCancelada> listarCancelaciones() {
        return service.obtenerTodas();
    }


    @PostMapping
    public VentaCancelada guardarCancelacion(@RequestBody VentaCancelada venta) {

        return service.registrarCancelacion(venta);
    }
}