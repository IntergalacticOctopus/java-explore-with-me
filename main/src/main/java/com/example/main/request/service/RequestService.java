package com.example.main.request.service;


import com.example.main.events.dto.*;
import com.example.main.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    EventRequestStatusUpdateResult patchRequests(EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest, int userId, int eventId);


    ParticipationRequestDto postRequest(int userId, int eventId);

    List<ParticipationRequestDto> getRequests(int userId);

    ParticipationRequestDto cancelRequest(int userId, int requestId);
}