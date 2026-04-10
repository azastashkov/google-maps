package com.google.maps.routeplanner.client;

import com.google.maps.routeplanner.dto.EtaRequest;
import com.google.maps.routeplanner.dto.EtaResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class EtaClient {

    private final RestClient restClient;

    public EtaClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public EtaResponse getEta(EtaRequest request) {
        return restClient.post()
                .uri("http://eta-service/api/v1/eta")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(EtaResponse.class);
    }
}
