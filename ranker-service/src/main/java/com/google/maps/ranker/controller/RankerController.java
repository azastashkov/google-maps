package com.google.maps.ranker.controller;

import com.google.maps.ranker.dto.RankerRequest;
import com.google.maps.ranker.dto.RankerResponse;
import com.google.maps.ranker.service.RankerService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rank")
public class RankerController {

    private final RankerService rankerService;

    public RankerController(RankerService rankerService) {
        this.rankerService = rankerService;
    }

    @PostMapping
    public RankerResponse rank(@RequestBody RankerRequest request) {
        return rankerService.rankRoutes(request.getRoutes(), request.getK());
    }
}
