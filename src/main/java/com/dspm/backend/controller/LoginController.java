package com.dspm.backend.controller;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class LoginController {

    @Value("${auth.base-url}")
    private String authBaseUrl;

    private final WebClient webClient;

    public LoginController() {
        this.webClient = WebClient.builder()
                .codecs(cfg -> cfg.defaultCodecs().maxInMemorySize(1024 * 1024))
                .build();
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<Object>> login(@RequestBody Map<String, Object> body) {
        return webClient.post()
                .uri(authBaseUrl + "/auth/login")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Object.class)
                .timeout(Duration.ofSeconds(10))
                .map(ResponseEntity::ok)
                .onErrorResume(WebClientException.class, ex -> {
                    Map<String, String> errorResponse = new HashMap<>();
                    if (ex.getMessage().contains("Connection refused")) {
                        errorResponse.put("error", "Auth service unreachable");
                        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse));
                    } else {
                        errorResponse.put("error", "Auth service timeout");
                        return Mono.just(ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(errorResponse));
                    }
                })
                .onErrorResume(Exception.class, ex -> {
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Error: " + ex.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
                });
    }

    @PostMapping("/verify-token")
    public Mono<ResponseEntity<Object>> verifyToken(@RequestBody Map<String, Object> body) {
        return webClient.post()
                .uri(authBaseUrl + "/auth/verify-token")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Object.class)
                .timeout(Duration.ofSeconds(10))
                .map(ResponseEntity::ok)
                .onErrorResume(WebClientException.class, ex -> {
                    Map<String, String> errorResponse = new HashMap<>();
                    if (ex.getMessage().contains("Connection refused")) {
                        errorResponse.put("error", "Auth service unreachable");
                        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse));
                    } else {
                        errorResponse.put("error", "Auth service timeout");
                        return Mono.just(ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(errorResponse));
                    }
                })
                .onErrorResume(Exception.class, ex -> {
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Error: " + ex.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
                });
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<Object>> logout(@RequestBody Map<String, Object> body) {
        return webClient.post()
                .uri(authBaseUrl + "/auth/logout")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Object.class)
                .timeout(Duration.ofSeconds(10))
                .map(ResponseEntity::ok)
                .onErrorResume(WebClientException.class, ex -> {
                    Map<String, String> errorResponse = new HashMap<>();
                    if (ex.getMessage().contains("Connection refused")) {
                        errorResponse.put("error", "Auth service unreachable");
                        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse));
                    } else {
                        errorResponse.put("error", "Auth service timeout");
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
