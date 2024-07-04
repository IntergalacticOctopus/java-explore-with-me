package ru.practicum.client;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import ru.practicum.HitDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class StatClient extends BaseClient {
    private static final String START = "start";
    private static final String END = "end";

    @Autowired
    public StatClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> saveHit(HitDto endpointHitDto) {
        return post("/hit", endpointHitDto);
    }

    public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        final String stringStart = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        final String stringEnd = end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        final Map<String, Object> parameters = Map.of(
                START, stringStart,
                END, stringEnd
        );
        final StringJoiner pathBuilder = new StringJoiner("&", "/stats?start={start}&end={end}", "");
        if (!uris.isEmpty()) {
            uris.forEach(uri -> pathBuilder.add("&uris=" + uri));
        }
        if (Objects.nonNull(unique)) {
            pathBuilder.add("&unique=" + unique);
        }
        final String path = pathBuilder.toString();
        return makeAndSendRequest(HttpMethod.GET, path, null, parameters, null);
    }
}