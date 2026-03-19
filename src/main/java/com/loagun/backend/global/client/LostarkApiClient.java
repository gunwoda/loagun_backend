package com.loagun.backend.global.client;

import com.loagun.backend.global.common.exception.CustomException;
import com.loagun.backend.global.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;
import java.util.function.Function;

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

    public <T> T get(String pathTemplate, Class<T> responseType, Map<String, Object> uriVars) {
        return webClient.get()
                .uri(pathTemplate, uriVars)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    HttpStatus status = HttpStatus.resolve(response.statusCode().value());
                    log.warn("Lostark API 4xx: {} {}", status, pathTemplate);
                    return Mono.error(resolveClientError(status));
                })
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    log.error("Lostark API 5xx: {} {}", response.statusCode(), pathTemplate);
                    return Mono.error(new CustomException(ErrorCode.EXTERNAL_API_ERROR));
                })
                .bodyToMono(responseType)
                .block();
    }

    public <T> T post(String pathTemplate, Object body, Class<T> responseType, Map<String, Object> uriVars) {
        return webClient.post()
                .uri(pathTemplate, uriVars)
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    HttpStatus status = HttpStatus.resolve(response.statusCode().value());
                    log.warn("Lostark API 4xx: {} {}", status, pathTemplate);
                    return Mono.error(resolveClientError(status));
                })
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    log.error("Lostark API 5xx: {} {}", response.statusCode(), pathTemplate);
                    return Mono.error(new CustomException(ErrorCode.EXTERNAL_API_ERROR));
                })
                .bodyToMono(responseType)
                .block();
    }

    /**
     * GET 요청 - UriBuilder 함수 방식 (쿼리 파라미터가 있거나 선택적인 경우)
     */
    public <T> T get(Function<UriBuilder, URI> uriFunction, Class<T> responseType) {
        return webClient.get()
                .uri(uriFunction)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    HttpStatus status = HttpStatus.resolve(response.statusCode().value());
                    log.warn("Lostark API 4xx: {}", status);
                    return Mono.error(resolveClientError(status));
                })
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    log.error("Lostark API 5xx: {}", response.statusCode());
                    return Mono.error(new CustomException(ErrorCode.EXTERNAL_API_ERROR));
                })
                .bodyToMono(responseType)
                .block();
    }

    private CustomException resolveClientError(HttpStatus status) {
        if (status == null) return new CustomException(ErrorCode.EXTERNAL_API_ERROR);
        return switch (status) {
            case NOT_FOUND -> new CustomException(ErrorCode.CHARACTER_NOT_FOUND);
            case TOO_MANY_REQUESTS -> new CustomException(ErrorCode.RATE_LIMIT_EXCEEDED);
            default -> new CustomException(ErrorCode.EXTERNAL_API_ERROR);
        };
    }
}
