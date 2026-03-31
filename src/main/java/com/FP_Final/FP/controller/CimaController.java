package com.FP_Final.FP.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/cima") // Prefijo para tu API interna
@CrossOrigin(origins = "*") // Permite que tu Angular le hable
public class CimaController {

    private final String AEMPS_URL = "https://cima.aemps.es/cima/rest/medicamentos";

    @GetMapping("/medicamento")
    public ResponseEntity<String> getMedicamento(@RequestParam String nregistro) {
        String urlDestino = "https://cima.aemps.es/cima/rest/medicamento?nregistro=" + nregistro;
        RestTemplate restTemplate = new RestTemplate();
        try {
            String respuesta = restTemplate.getForObject(urlDestino, String.class);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\": \"Error conectando con AEMPS\"}");
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<String> buscarMedicamentos(
            @RequestParam String nombre,
            @RequestParam(defaultValue = "1") int pagina) {

        // 1. Construimos la URL real del Ministerio
        String urlDestino = AEMPS_URL + "?nombre=" + nombre + "&pagina=" + pagina;

        // 2. Usamos RestTemplate para hacer la petición de Servidor a Servidor
        RestTemplate restTemplate = new RestTemplate();
        try {
            // El backend pide los datos (esto NO tiene bloqueo CORS)
            String respuesta = restTemplate.getForObject(urlDestino, String.class);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\": \"Error conectando con AEMPS\"}");
        }
    }
}