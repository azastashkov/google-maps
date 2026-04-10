package com.google.maps.eta.controller;

import com.google.maps.eta.dto.EtaRequest;
import com.google.maps.eta.dto.EtaResponse;
import com.google.maps.eta.service.EtaService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/eta")
public class EtaController {

    private final EtaService etaService;

    public EtaController(EtaService etaService) {
        this.etaService = etaService;
    }

    @PostMapping
    public EtaResponse calculateEta(@RequestBody EtaRequest request) {
        return etaService.calculateEta(request.getWaypoints(), request.getDepartureTime());
    }
}
