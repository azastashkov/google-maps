package com.google.maps.routeplanner.client;

import com.google.maps.routeplanner.dto.EtaRequest;
import com.google.maps.routeplanner.dto.EtaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class EtaClient {

    private final RestTemplate restTemplate;

    public EtaResponse getEta(EtaRequest request) {
        return restTemplate.postForObject(
                "http://eta-service/api/v1/eta",
                request,
                EtaResponse.class);
    }
}
