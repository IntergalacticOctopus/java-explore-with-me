package com.example.statsserver.controller;

import com.example.statsserver.service.StatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.HitDto;
import ru.practicum.StatDto;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatController {
    private final StatService statService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveHit(@RequestBody HitDto hitDto) {
        log.info("Post request /hit hitDto = {}", hitDto);
        statService.saveHit(hitDto);
    }

    @GetMapping("/stats")
    public List<StatDto> getEventsInfo(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(defaultValue = "false") boolean unique,
            @RequestParam @Nullable List<String> uris) {
        log.info("Get request /stats start = {}, end = {}, uris = {}, unique = {}", start, end, uris, unique);
        return statService.getStatInfo(start, end, uris, unique);
    }
}
