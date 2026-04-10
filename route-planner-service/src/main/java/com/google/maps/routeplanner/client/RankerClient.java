package com.google.maps.routeplanner.client;

import com.google.maps.routeplanner.dto.RankerRequest;
import com.google.maps.routeplanner.dto.RankerResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RankerClient {

    private final RestTemplate restTemplate;

    public RankerClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public RankerResponse rank(RankerRequest request) {
        return restTemplate.postForObject(
                "http://ranker-service/api/v1/rank",
                request,
                RankerResponse.class);
    }
}
