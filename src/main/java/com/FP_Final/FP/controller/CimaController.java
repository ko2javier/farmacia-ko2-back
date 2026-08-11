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

    // GET /api/cima/medicamento — proxy para obtener ficha completa de un medicamento AEMPS por número de registro
    @GetMapping("/medicamento")
    public ResponseEntity<String> obtenerMedicamento(@RequestParam String nregistro) {
        String urlDestino = "https://cima.aemps.es/cima/rest/medicamento?nregistro=" + nregistro;
        try {
            String respuesta = new RestTemplate().getForObject(urlDestino, String.class);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            throw new ExternalServiceException("Error conectando con AEMPS");
        }
    }

    // GET /api/cima/buscar — busca medicamentos en AEMPS por nombre (con paginación)
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

    // GET /api/cima/validar — comprueba si existe en inventario, consulta AEMPS y llama al microservicio IA
    @GetMapping("/validar")
    public ResponseEntity<?> validarMedicamento(@RequestParam String nombre) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // 1. Si ya existe en inventario, no tiene sentido continuar
        if (!articulosRepository.findByNombreContaining(nombre).isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("mensaje", "El producto ya existe en el inventario"));
        }

        // 2. Consultamos AEMPS con hasta 100 resultados para dar margen al motor IA
        String urlAemps = AEMPS_URL + "?nombre=" + nombre + "&pagina=1&tamanioPagina=100";
        Map<String, Object> respuestaAemps;
        try {
            respuestaAemps = new RestTemplate().getForObject(urlAemps, Map.class);
        } catch (Exception e) {
            throw new ExternalServiceException("Error conectando con AEMPS");
        }

        // 3. Convertimos los resultados crudos de AEMPS al DTO limpio que entiende la IA
        List<MedicamentoAempsDTO> listaMedicamentos = new ArrayList<>();
        if (respuestaAemps != null && respuestaAemps.containsKey("resultados")) {
            List<Map<String, Object>> resultados = (List<Map<String, Object>>) respuestaAemps.get("resultados");
            for (Map<String, Object> medicamento : resultados) {
                String id = medicamento.get("nregistro") != null ? medicamento.get("nregistro").toString() : "";
                String nombreMedicamento = medicamento.get("nombre") != null ? medicamento.get("nombre").toString() : "";
                listaMedicamentos.add(new MedicamentoAempsDTO(id, nombreMedicamento));
            }
        }

        // 4. Sin resultados → el medicamento no existe en AEMPS
        if (listaMedicamentos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", "No se encontraron resultados en AEMPS"));
        }

        // 5. Enviamos al microservicio IA para que encuentre la mejor coincidencia
        Map<String, Object> resultadoIa = iaService.llamarMicroservicioIa(nombre, listaMedicamentos);

        // 6. Registramos la búsqueda IA en el historial de actividad
        activityLogService.log(username, "IA_MATCH", nombre, request.getRemoteAddr());

        return ResponseEntity.ok(resultadoIa);
    }
}
