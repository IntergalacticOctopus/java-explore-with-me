package com.example.main.events.controller;


import com.example.main.events.dto.*;
import com.example.main.events.service.PrivateEventService;
import com.example.main.exception.errors.InvalidRequestException;
import com.example.main.request.dto.ParticipationRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
@Validated
public class PrivateEventController {
    private final PrivateEventService privateEventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto post(@RequestBody @Valid NewEventDto newEventDto,
                             @PathVariable @PositiveOrZero int userId) {
        return privateEventService.postEvent(newEventDto, userId);
    }

    @GetMapping
    public List<EventShortDto> getUserEvents(@PathVariable @PositiveOrZero int userId,
                                             @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                             @RequestParam(defaultValue = "10") @Positive int size) {
        final PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
        return privateEventService.getUserEvents(userId, pageRequest);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getById(@PathVariable @PositiveOrZero int userId,
                                @PathVariable @PositiveOrZero int eventId) {
        return privateEventService.getEventById(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto patch(@RequestBody @Valid UpdateEventUserRequest updateEventUserRequest,
                              @PathVariable @PositiveOrZero int userId,
                              @PathVariable @PositiveOrZero int eventId) {
        if (updateEventUserRequest.getEventDate() != null) {
            if (LocalDateTime.now().plusHours(2).isAfter(updateEventUserRequest.getEventDate())) {
                throw new InvalidRequestException("Invalid date");
            }
        }
        return privateEventService.patchEvent(updateEventUserRequest, userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsInEvent(@PathVariable @PositiveOrZero int userId,
                                                            @PathVariable @PositiveOrZero int eventId) {
        return privateEventService.getRequestsInEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult patchRequests(@RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest,
                                                        @PathVariable @PositiveOrZero int userId,
                                                        @PathVariable @PositiveOrZero int eventId) {
        return privateEventService.patchRequests(eventRequestStatusUpdateRequest, userId, eventId);
    }
}