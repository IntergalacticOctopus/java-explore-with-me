package ru.practicum.ewm.client;

import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.ewm.HitDto;
import ru.practicum.ewm.StatDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Component
public class StatClientImpl implements StatClient {
    private final RestTemplate rest;

    @Autowired
    public StatClientImpl(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        this.rest =
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build();
    }

    @Override
    public void saveHit(HitDto hitDto) {
        try {
            HttpEntity<HitDto> requestEntity = new HttpEntity<>(hitDto, defaultHeaders());
            ResponseEntity<Object> response = rest.exchange("/hit", HttpMethod.POST, requestEntity, Object.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Hit successfully added");
            } else {
                log.error("Hit does not added");
            }
        } catch (Exception e) {
            log.error("Hit does not added");
        }
    }

    @Override
    public List<StatDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                .fromPath("/stats")
                .queryParam("start", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(start))
                .queryParam("end", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(end))
                .queryParam("uris", uris)
                .queryParam("unique", unique);
        try {
            ResponseEntity<List<StatDto>> response = rest.exchange(
                    uriComponentsBuilder.build().toString(), HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                    }
            );
            if (response.getStatusCode().is2xxSuccessful()) {
                log.error("Successfully got stats");
                return response.getBody();
            } else {
                log.error("Failed while getting stats");
                return List.of();
            }
        } catch (Exception e) {
            log.error("Failed while getting stats");
            return List.of();
        }
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}