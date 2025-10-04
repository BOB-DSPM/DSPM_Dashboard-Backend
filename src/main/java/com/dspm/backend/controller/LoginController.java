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
    private String authBaseUrl; // 반드시 http://auth:3005/api 로 설정해야 함

    private final WebClient webClient;

    public LoginController() {
        this.webClient = WebClient.builder()
                .codecs(cfg -> cfg.defaultCodecs().maxInMemorySize(1024 * 1024))
                .build();
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<Object>> login(@RequestBody Map<String, Object> body) {
        return callAuthService("/auth/login", body);
    }

    @PostMapping("/verify-token")
    public Mono<ResponseEntity<Object>> verifyToken(@RequestBody Map<String, Object> body) {
        return callAuthService("/auth/verify-token", body);
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<Object>> logout(@RequestBody Map<String, Object> body) {
        return callAuthService("/auth/logout", body);
    }

    private Mono<ResponseEntity<Object>> callAuthService(String path, Map<String, Object> body) {
        return webClient.post()
                .uri(authBaseUrl + path)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Object.class)
                .timeout(Duration.ofSeconds(15))
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
