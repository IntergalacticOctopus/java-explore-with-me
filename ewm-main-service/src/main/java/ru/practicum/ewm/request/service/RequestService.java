package ru.practicum.ewm.request.service;


import ru.practicum.ewm.events.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.events.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    EventRequestStatusUpdateResult patchRequests(EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest, int userId, int eventId);


    ParticipationRequestDto postRequest(int userId, int eventId);

    List<ParticipationRequestDto> getRequests(int userId);

    ParticipationRequestDto cancelRequest(int userId, int requestId);

    List<ParticipationRequestDto> getRequestsInEvent(int userId, int eventId);
}