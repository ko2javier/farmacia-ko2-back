package com.FP_Final.FP.service;

import com.FP_Final.FP.model.VentaCancelada;
import com.FP_Final.FP.repository.VentaCanceladaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VentaCanceladaService {

    @Autowired
    private VentaCanceladaRepository repository;

    // Método para guardar una cancelación
    public VentaCancelada registrarCancelacion(VentaCancelada venta) {
        return repository.save(venta);
    }

    // Método para obtener el historial completo
    public List<VentaCancelada> obtenerTodas() {
        // Aquí podríamos ordenar por fecha descendente para ver las últimas primero
        return repository.findAll();
    }
}