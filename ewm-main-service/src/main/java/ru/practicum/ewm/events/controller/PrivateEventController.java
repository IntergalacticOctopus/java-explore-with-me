package ru.practicum.ewm.events.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.events.dto.*;
import ru.practicum.ewm.events.service.EventService;
import ru.practicum.ewm.exception.errors.InvalidRequestException;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;

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
    private final EventService eventService;
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto post(@RequestBody @Valid NewEventDto newEventDto,
                             @PathVariable @PositiveOrZero int userId) {
        return eventService.postEvent(newEventDto, userId);
    }

    @GetMapping
    public List<EventShortDto> getUserEvents(@PathVariable @PositiveOrZero int userId,
                                             @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                             @RequestParam(defaultValue = "10") @Positive int size) {
        final PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
        return eventService.getUserEvents(userId, pageRequest);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getById(@PathVariable @PositiveOrZero int userId,
                                @PathVariable @PositiveOrZero int eventId) {
        return eventService.getEventById(userId, eventId);
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
        return eventService.patchEvent(updateEventUserRequest, userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsInEvent(@PathVariable @PositiveOrZero int userId,
                                                            @PathVariable @PositiveOrZero int eventId) {
        return requestService.getRequestsInEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult patchRequests(@RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest,
                                                        @PathVariable @PositiveOrZero int userId,
                                                        @PathVariable @PositiveOrZero int eventId) {
        return requestService.patchRequests(eventRequestStatusUpdateRequest, userId, eventId);
    }
}