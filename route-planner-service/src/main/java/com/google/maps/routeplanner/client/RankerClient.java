package com.google.maps.routeplanner.client;

import com.google.maps.routeplanner.dto.RankerRequest;
import com.google.maps.routeplanner.dto.RankerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class RankerClient {

    private final RestTemplate restTemplate;

    public RankerResponse rank(RankerRequest request) {
        return restTemplate.postForObject(
                "http://ranker-service/api/v1/rank",
                request,
                RankerResponse.class);
    }
}
