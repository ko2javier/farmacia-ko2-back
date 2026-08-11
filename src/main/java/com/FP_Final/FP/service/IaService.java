package com.FP_Final.FP.service;

import com.FP_Final.FP.model.MatchRequestDTO;
import com.FP_Final.FP.model.MedicamentoAempsDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class IaService {

    // URL del microservicio Python/FastAPI — se configura en application.properties
    @Value("${ia.service.url}")
    private String iaServiceUrl;

    // Clave API para autenticarse con el microservicio IA
    @Value("${ia.service.api-key}")
    private String iaServiceApiKey;

    // Envía el nombre buscado y la lista AEMPS al microservicio IA para que encuentre la mejor coincidencia
    public Map<String, Object> llamarMicroservicioIa(String busqueda, List<MedicamentoAempsDTO> listaMedicamentos) {
        MatchRequestDTO cuerpoRequest = new MatchRequestDTO(busqueda, listaMedicamentos);

        HttpHeaders cabeceras = new HttpHeaders();
        cabeceras.setContentType(MediaType.APPLICATION_JSON);
        cabeceras.set("x-api-key", iaServiceApiKey);

        HttpEntity<MatchRequestDTO> peticion = new HttpEntity<>(cuerpoRequest, cabeceras);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> respuesta = restTemplate.exchange(
                iaServiceUrl + "/api/v1/ia/match",
                HttpMethod.POST,
                peticion,
                Map.class
        );

        return respuesta.getBody();
    }
}
