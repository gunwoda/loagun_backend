package com.loagun.backend.global.client;

import com.loagun.backend.global.common.exception.CustomException;
import com.loagun.backend.global.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

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

    /**
     * GET 요청 - URI 템플릿 + 변수 방식으로 WebClient가 인코딩을 담당
     * pathTemplate 예: "/armories/characters/{characterName}/profiles"
     * uriVars 예: Map.of("characterName", "깜지직")
     */
    public <T> T get(String pathTemplate, Class<T> responseType, Map<String, Object> uriVars) {
        return webClient.get()
                .uri(pathTemplate, uriVars)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    log.warn("Lostark API 4xx error: {} {}", response.statusCode(), pathTemplate);
                    return Mono.error(new CustomException(ErrorCode.CHARACTER_NOT_FOUND));
                })
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    log.error("Lostark API 5xx error: {} {}", response.statusCode(), pathTemplate);
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
                    log.warn("Lostark API 4xx error: {} {}", response.statusCode(), pathTemplate);
                    return Mono.error(new CustomException(ErrorCode.EXTERNAL_API_ERROR));
                })
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    log.error("Lostark API 5xx error: {} {}", response.statusCode(), pathTemplate);
                    return Mono.error(new CustomException(ErrorCode.EXTERNAL_API_ERROR));
                })
                .bodyToMono(responseType)
                .block();
    }
}
