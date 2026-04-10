package com.google.maps.routeplanner.client;

import com.google.maps.routeplanner.dto.EtaRequest;
import com.google.maps.routeplanner.dto.EtaResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class EtaClient {

    private final RestTemplate restTemplate;

    public EtaClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public EtaResponse getEta(EtaRequest request) {
        return restTemplate.postForObject(
                "http://eta-service/api/v1/eta",
                request,
                EtaResponse.class);
    }
}
