package com.dspm.backend.controller;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/analyzer")
@CrossOrigin(origins = "*")
public class AnalyzerController {

    @Value("${analyzer.base-url}")
    private String analyzerBaseUrl;

    private final WebClient webClient;

    public AnalyzerController() {
        this.webClient = WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .build();
    }

    @GetMapping("/health")
    public Mono<ResponseEntity<Object>> getAnalyzerHealth() {
        return webClient.get()
                .uri(analyzerBaseUrl + "/health")
                .retrieve()
                .bodyToMono(Object.class)
                .timeout(Duration.ofSeconds(10))
                .map(ResponseEntity::ok)
                .onErrorResume(WebClientException.class, ex -> {
                    Map<String, String> errorResponse = new HashMap<>();
                    if (ex.getMessage().contains("Connection refused")) {
                        errorResponse.put("error", "Analyzer service unreachable");
                        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse));
                    } else {
                        errorResponse.put("error", "Analyzer service timeout");
                        return Mono.just(ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(errorResponse));
                    }
                })
                .onErrorResume(Exception.class, ex -> {
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Error: " + ex.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
                });
    }
}