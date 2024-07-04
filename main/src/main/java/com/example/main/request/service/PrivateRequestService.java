package com.example.main.request.service;


import com.example.main.request.dto.ParticipationRequestDto;

import java.util.List;

public interface PrivateRequestService {
    ParticipationRequestDto postRequest(int userId, int eventId);

    List<ParticipationRequestDto> getRequests(int userId);

    ParticipationRequestDto cancelRequest(int userId, int requestId);
}