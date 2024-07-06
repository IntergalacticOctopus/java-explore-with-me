package com.example.main.events.service;


import com.example.main.events.dto.*;
import com.example.main.request.dto.ParticipationRequestDto;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface PrivateEventService {
    EventFullDto postEvent(NewEventDto newEventDto, int userId);

    List<EventShortDto> getUserEvents(int userId, PageRequest pageRequest);

    EventFullDto getEventById(int userId, int eventId);

    EventFullDto patchEvent(UpdateEventUserRequest updateEventUserRequest, int userId, int eventId);

    List<ParticipationRequestDto> getRequestsInEvent(int userId, int eventId);

    EventRequestStatusUpdateResult patchRequests(EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest, int userId, int eventId);
}