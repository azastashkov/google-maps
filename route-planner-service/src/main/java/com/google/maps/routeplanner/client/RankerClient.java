package com.google.maps.routeplanner.client;

import com.google.maps.routeplanner.dto.RankerRequest;
import com.google.maps.routeplanner.dto.RankerResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class RankerClient {

    private final RestClient restClient;

    public RankerClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public RankerResponse rank(RankerRequest request) {
        return restClient.post()
                .uri("http://ranker-service/api/v1/rank")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(RankerResponse.class);
    }
}
