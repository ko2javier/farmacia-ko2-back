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

    @Value("${ia.service.url}")
    private String iaServiceUrl;

    @Value("${ia.service.api-key}")
    private String iaServiceApiKey;

    public Map<String, Object> llamarMicroservicioIa(String busqueda, List<MedicamentoAempsDTO> lista) {
        MatchRequestDTO requestBody = new MatchRequestDTO(busqueda, lista);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", iaServiceApiKey);

        HttpEntity<MatchRequestDTO> entity = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.exchange(
                iaServiceUrl + "/api/v1/ia/match",
                HttpMethod.POST,
                entity,
                Map.class
        );

        return response.getBody();
    }
}
