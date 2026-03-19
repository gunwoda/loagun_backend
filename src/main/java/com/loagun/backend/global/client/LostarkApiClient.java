package com.loagun.backend.global.client;

import com.loagun.backend.global.common.exception.CustomException;
import com.loagun.backend.global.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class LostarkApiClient {

    private final WebClient webClient;

    public LostarkApiClient(
            WebClient.Builder webClientBuilder,
            @Value("${lostark.api.base-url}") String baseUrl,
            @Value("${lostark.api.key}") String apiKey
    ) {
        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "bearer " + apiKey)
                .defaultHeader("Accept", "application/json")
                .build();
    }

    public <T> T get(String path, Class<T> responseType) {
        return webClient.get()
                .uri(path)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    log.warn("Lostark API 4xx error: {} {}", response.statusCode(), path);
                    return Mono.error(new CustomException(ErrorCode.CHARACTER_NOT_FOUND));
                })
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    log.error("Lostark API 5xx error: {} {}", response.statusCode(), path);
                    return Mono.error(new CustomException(ErrorCode.EXTERNAL_API_ERROR));
                })
                .bodyToMono(responseType)
                .block();
    }

    public <T> T post(String path, Object body, Class<T> responseType) {
        return webClient.post()
                .uri(path)
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    log.warn("Lostark API 4xx error: {} {}", response.statusCode(), path);
                    return Mono.error(new CustomException(ErrorCode.EXTERNAL_API_ERROR));
                })
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    log.error("Lostark API 5xx error: {} {}", response.statusCode(), path);
                    return Mono.error(new CustomException(ErrorCode.EXTERNAL_API_ERROR));
                })
                .bodyToMono(responseType)
                .block();
    }
}
