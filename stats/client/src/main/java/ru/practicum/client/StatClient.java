package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.HitDto;
import ru.practicum.StatDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class StatClient {

    @Autowired
    public StatClient(@Value("${stat-server.url}") String serverUrl) {
        this.restTemplate = new RestTemplate();
        this.serverUrl = serverUrl;
    }

    private final RestTemplate restTemplate;
    private final String serverUrl;

    public void saveHit(HitDto hitDto) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<HitDto> entity = new HttpEntity<>(hitDto, headers);
            ResponseEntity<Void> response = restTemplate.postForEntity(serverUrl + "/hit", entity, Void.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to save hit: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("Error saving hit: " + e.getMessage());
        }
    }

    public List<StatDto> getStats(LocalDateTime start, LocalDateTime end, Boolean unique, List<String> uris) {
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "unique", unique,
                "uris", uris
        );

        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(serverUrl + "/stats")
                    .queryParam("start", start)
                    .queryParam("end", end)
                    .queryParam("unique", unique)
                    .queryParam("uris", String.join(",", uris));

            ResponseEntity<List<StatDto>> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, null, new ParameterizedTypeReference<List<StatDto>>() {});
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to get stats: " + response.getStatusCode());
            }
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Error getting stats: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}