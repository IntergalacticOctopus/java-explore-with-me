package com.example.main.request.service;

import com.example.main.events.model.Event;
import com.example.main.events.model.EventState;
import com.example.main.events.repository.EventRepository;
import com.example.main.exception.model.DataConflictException;
import com.example.main.exception.model.EntityNotFoundException;
import com.example.main.exception.model.ForbiddenOperationException;
import com.example.main.request.dto.ParticipationRequestDto;
import com.example.main.request.mapper.RequestMapper;
import com.example.main.request.model.Request;
import com.example.main.request.model.RequestStatus;
import com.example.main.request.repository.RequestRepository;
import com.example.main.user.model.User;
import com.example.main.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrivateRequestServiceImpl implements PrivateRequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;

    @Override
    @Transactional
    public ParticipationRequestDto postRequest(int userId, int eventId) {
        final User userFromDb = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Data not found"));
        final Event eventFromDb = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Data not found")));
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new DataConflictException("User already exists");
        }
        if (userId == eventFromDb.getInitiator().getId()) {
            throw new DataConflictException("User is owner of the event");
        }
        if (!eventFromDb.getState().equals(EventState.PUBLISHED)) {
            throw new DataConflictException("Event is not published");
        }
        int countRequestByEventIdAndStatus = requestRepository.countRequestByEventIdAndStatus(
                eventId,
                RequestStatus.CONFIRMED
        );
        if (eventFromDb.getParticipantLimit() > 0 &&
                countRequestByEventIdAndStatus >= eventFromDb.getParticipantLimit()) {
            throw new DataConflictException("Event with is full");
        }
        final Request request = requestMapper.toRequest(userFromDb, eventFromDb);
        return requestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequests(int userId) {

        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Data not found");
        }

        return requestRepository.getRequestsByRequesterId(userId).stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(int userId, int requestId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Data not found");
        }
        final Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Data not found"));
        if (request.getRequester().getId() != userId) {
            throw new ForbiddenOperationException("User is not owner");
        }
        request.setStatus(RequestStatus.CANCELED);
        return requestMapper.toParticipationRequestDto(requestRepository.save(request));
    }
}