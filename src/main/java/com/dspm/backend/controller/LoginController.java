package com.dspm.backend.controller;

import java.time.Duration;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
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

    // 로그인
    @PostMapping("/login")
    public Mono<ResponseEntity<Object>> login(@RequestBody Map<String, Object> body) {
        return webClient.post()
                .uri(authBaseUrl + "/auth/login")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Object.class)
                .timeout(Duration.ofSeconds(10))
                .map(ResponseEntity::ok)
                .onErrorResume(ex ->
                        Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                                .body(Map.of("error", "Auth service error: " + ex.getMessage())))
                );
    }

    // 토큰 검증
    @PostMapping("/verify-token")
    public Mono<ResponseEntity<Object>> verifyToken(@RequestBody Map<String, Object> body) {
        return webClient.post()
                .uri(authBaseUrl + "/auth/verify-token")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Object.class)
                .timeout(Duration.ofSeconds(10))
                .map(ResponseEntity::ok)
                .onErrorResume(ex ->
                        Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                                .body(Map.of("error", "Auth service error: " + ex.getMessage())))
                );
    }

    // 로그아웃
    @PostMapping("/logout")
    public Mono<ResponseEntity<Object>> logout(@RequestBody Map<String, Object> body) {
        return webClient.post()
                .uri(authBaseUrl + "/auth/logout")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Object.class)
                .timeout(Duration.ofSeconds(10))
                .map(ResponseEntity::ok)
                .onErrorResume(ex ->
                        Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                                .body(Map.of("error", "Auth service error: " + ex.getMessage())))
                );
    }
}
