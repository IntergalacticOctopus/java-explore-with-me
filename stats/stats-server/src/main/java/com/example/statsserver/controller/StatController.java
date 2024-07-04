package com.example.statsserver.controller;

import com.example.statsserver.service.StatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.HitDto;
import ru.practicum.StatDto;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.statsserver.mapper.Mapper.DATETIME_FORMAT;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatController {
    private final StatService statService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/hit")
    public HitDto saveHit(@RequestBody HitDto hitDto) {
        log.info("Post request /hit hitDto = {}", hitDto);
        return statService.saveHit(hitDto);
    }

    @GetMapping("/stats")
    public List<StatDto> getStats(@RequestParam @DateTimeFormat(pattern = DATETIME_FORMAT) LocalDateTime start,
                                  @RequestParam @DateTimeFormat(pattern = DATETIME_FORMAT) LocalDateTime end,
                                  @RequestParam(defaultValue = "") List<String> uris,
                                  @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Get request /stats start = {}, end = {}, uris = {}, unique = {}", start, end, uris, unique);
        return statService.getStats(start, end, uris, unique);
    }
}
