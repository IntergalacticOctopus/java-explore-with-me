package ru.practicum.ewm.events.controller;

import ru.practicum.ewm.client.StatClient;
import ru.practicum.ewm.events.service.EventService;
import ru.practicum.ewm.events.model.PublicEventParam;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.model.SortConflict;
import ru.practicum.ewm.exception.errors.InvalidRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.HitDto;

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
        if (!sort.equalsIgnoreCase(String.valueOf(SortConflict.EVENT_DATE)) && !sort.equalsIgnoreCase(String.valueOf(SortConflict.VIEWS))) {
            throw new InvalidRequestException("Invalid sorting");
        }
        PublicEventParam publicEventParam = new PublicEventParam(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort);
        List<EventShortDto> eventShortDtos = eventService.getShortEvents(publicEventParam, page);
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
        EventFullDto eventFullDto = eventService.getEventById(id);
        statClient.saveHit(hitDto);
        return eventFullDto;
    }
}