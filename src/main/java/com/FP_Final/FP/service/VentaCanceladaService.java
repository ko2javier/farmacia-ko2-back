package com.FP_Final.FP.service;

import com.FP_Final.FP.model.VentaCancelada;
import com.FP_Final.FP.repository.VentaCanceladaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VentaCanceladaService {

    @Autowired
    private VentaCanceladaRepository ventaCanceladaRepository;

    // Guarda una cancelación en el historial (llamado desde VentaService al cancelar una venta)
    public VentaCancelada registrarCancelacion(VentaCancelada ventaCancelada) {
        return ventaCanceladaRepository.save(ventaCancelada);
    }

    // Devuelve todas las cancelaciones registradas (para el historial en el frontend)
    public List<VentaCancelada> obtenerTodas() {
        return ventaCanceladaRepository.findAll();
    }
}
