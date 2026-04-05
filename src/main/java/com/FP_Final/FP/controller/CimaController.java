package com.FP_Final.FP.controller;

import com.FP_Final.FP.exception.ExternalServiceException;
import com.FP_Final.FP.model.MedicamentoAempsDTO;
import com.FP_Final.FP.repository.ArticulosRepository;
import com.FP_Final.FP.service.ActivityLogService;
import com.FP_Final.FP.service.IaService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cima")
@CrossOrigin(origins = "*")
public class CimaController {

    private final String AEMPS_URL = "https://cima.aemps.es/cima/rest/medicamentos";

    @Autowired
    private ArticulosRepository articulosRepository;

    @Autowired
    private IaService iaService;

    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    private HttpServletRequest request;

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

    @GetMapping("/validar")
    public ResponseEntity<?> validarMedicamento(@RequestParam String nombre) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // 1. Comprobar si ya existe en inventario
        if (!articulosRepository.findByNombreContaining(nombre).isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("mensaje", "El producto ya existe en el inventario"));
        }

        // 2. Consultar AEMPS
        System.out.println("Llamando a AEMPS con nombre: " + nombre);
        String urlAemps = AEMPS_URL + "?nombre=" + nombre + "&pagina=1&tamanioPagina=100";
        Map<String, Object> aempsResponse;
        try {
            aempsResponse = new RestTemplate().getForObject(urlAemps, Map.class);
        } catch (Exception e) {
            throw new ExternalServiceException("Error conectando con AEMPS");
        }

        // 3. Mapear resultados a List<MedicamentoAempsDTO>
        List<MedicamentoAempsDTO> lista = new ArrayList<>();
        if (aempsResponse != null && aempsResponse.containsKey("resultados")) {
            List<Map<String, Object>> resultados = (List<Map<String, Object>>) aempsResponse.get("resultados");
            for (Map<String, Object> item : resultados) {
                String id = item.get("nregistro") != null ? item.get("nregistro").toString() : "";
                String nombreMed = item.get("nombre") != null ? item.get("nombre").toString() : "";
                lista.add(new MedicamentoAempsDTO(id, nombreMed));
            }
        }
        System.out.println("Resultados AEMPS: " + lista.size());

        // 4. Lista vacía → 404
        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", "No se encontraron resultados en AEMPS"));
        }

        // 5. Llamar al microservicio IA
        Map<String, Object> iaResult = iaService.llamarMicroservicioIa(nombre, lista);

        // 6. Registrar en activity_log
        activityLogService.log(username, "IA_MATCH", nombre, request.getRemoteAddr());

        return ResponseEntity.ok(iaResult);
    }
}
