package com.example.main.events.controller;

import com.example.main.events.dto.EventFullDto;
import com.example.main.events.dto.EventShortDto;
import com.example.main.events.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.HitDto;
import ru.practicum.client.StatClient;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
@ComponentScan(basePackages = {"ru.practicum.StatClient"})
public class PublicEventController {
    private final StatClient statClient;
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getEvents(@RequestParam(defaultValue = "") String text,
                                         @RequestParam(required = false) List<Integer> categories,
                                         @RequestParam(defaultValue = "false") Boolean paid,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                         @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                         @RequestParam(defaultValue = "EVENT_DATE") String sort,
                                         @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                         @RequestParam(defaultValue = "10") @Positive int size,
                                         HttpServletRequest request) {
        final HitDto endpointHitDto = HitDto.builder()
                .app("main-service")
                .uri("/events")
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).build();
        final PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<EventShortDto> eventShortDtos = eventService.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, page);
        statClient.saveHit(endpointHitDto);
        return eventShortDtos;
    }

    @GetMapping("/{id}")
    public EventFullDto getEventById(@PathVariable @PositiveOrZero int id,
                                     HttpServletRequest request) {
        final HitDto hitDto = HitDto.builder()
                .app("main-service")
                .uri("/events/" + id)
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).build();
        statClient.saveHit(hitDto);
        return eventService.getEventById(id);
    }
}