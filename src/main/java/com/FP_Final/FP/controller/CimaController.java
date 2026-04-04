package com.FP_Final.FP.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import com.FP_Final.FP.exception.ExternalServiceException;

@RestController
@RequestMapping("/api/cima") // Prefijo para tu API interna
@CrossOrigin(origins = "*") // Permite que tu Angular le hable
public class CimaController {

    private final String AEMPS_URL = "https://cima.aemps.es/cima/rest/medicamentos";

    @GetMapping("/medicamento")
    public ResponseEntity<String> getMedicamento(@RequestParam String nregistro) {
        String urlDestino = "https://cima.aemps.es/cima/rest/medicamento?nregistro=" + nregistro;
        try {
            String respuesta = new RestTemplate().getForObject(urlDestino, String.class);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            throw new ExternalServiceException("Error conectando con AEMPS");
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<String> buscarMedicamentos(
            @RequestParam String nombre,
            @RequestParam(defaultValue = "1") int pagina) {

        String urlDestino = AEMPS_URL + "?nombre=" + nombre + "&pagina=" + pagina;
        try {
            String respuesta = new RestTemplate().getForObject(urlDestino, String.class);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            throw new ExternalServiceException("Error conectando con AEMPS");
        }
    }
}