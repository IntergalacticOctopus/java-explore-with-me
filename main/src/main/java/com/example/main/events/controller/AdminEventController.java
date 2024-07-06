package com.example.main.events.controller;


import com.example.main.events.dto.EventFullDto;
import com.example.main.events.dto.UpdateEventAdminRequest;
import com.example.main.events.service.EventService;
import com.example.main.exception.errors.InvalidRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
@Validated
public class AdminEventController {
    private final EventService eventService;

    @PatchMapping("/{eventId}")
    public EventFullDto patch(@RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest,
                              @PathVariable @PositiveOrZero int eventId) {
        if (updateEventAdminRequest.getEventDate() != null) {
            if (LocalDateTime.parse(updateEventAdminRequest.getEventDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    .isBefore(LocalDateTime.now().plusHours(1))) {
                throw new InvalidRequestException("Invalid data");
            }
        }
        return eventService.patchEvent(updateEventAdminRequest, eventId);
    }

    @GetMapping
    public List<EventFullDto> get(@RequestParam(required = false) List<Integer> users,
                                  @RequestParam(required = false) List<String> states,
                                  @RequestParam(required = false) List<Integer> categories,
                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                  @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                  @RequestParam(defaultValue = "10") @Positive int size) {
        final PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
        return eventService.getEvents(users, states, categories, rangeStart, rangeEnd, pageRequest);
    }
}