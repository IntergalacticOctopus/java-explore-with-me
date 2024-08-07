package ru.practicum.ewm.request.service;

import ru.practicum.ewm.events.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.events.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.EventState;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.exception.errors.DataConflictException;
import ru.practicum.ewm.exception.errors.NotFoundException;
import ru.practicum.ewm.exception.errors.ForbiddenOperationException;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;


    @Override
    @Transactional
    public ParticipationRequestDto postRequest(int userId, int eventId) {
        final User userFromDb = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Data not found"));
        final Event eventFromDb = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Data not found")));
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
            throw new NotFoundException("Data not found");
        }

        return requestRepository.getRequestsByRequesterId(userId).stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(int userId, int requestId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Data not found");
        }
        final Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Data not found"));
        if (request.getRequester().getId() != userId) {
            throw new ForbiddenOperationException("User is not owner");
        }
        request.setStatus(RequestStatus.CANCELED);
        return requestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult patchRequests(EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest,
                                                        int userId,
                                                        int eventId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Data not found"));
        }
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException(String.format("Data not found"));
        }
        final List<Request> requests = requestRepository.findAllById(eventRequestStatusUpdateRequest.getRequestIds());
        switch (eventRequestStatusUpdateRequest.getStatus()) {
            case REJECTED:
                for (Request request : requests) {
                    if (request.getStatus() == RequestStatus.CONFIRMED) {
                        throw new DataConflictException("Invalid data");
                    }
                    request.setStatus(RequestStatus.REJECTED);
                }
                break;
            case CONFIRMED:
                for (Request request : requests) {
                    if (!request.getEvent().getRequestModeration() || request.getEvent().getParticipantLimit() == 0) {
                        request.setStatus(RequestStatus.CONFIRMED);
                        continue;
                    }
                    final int countRequestByEventIdAndStatus = requestRepository.countRequestByEventIdAndStatus(
                            request.getEvent().getId(),
                            RequestStatus.CONFIRMED
                    );
                    if (countRequestByEventIdAndStatus >= request.getEvent().getParticipantLimit()) {
                        request.setStatus(RequestStatus.REJECTED);
                        throw new DataConflictException("Invalid data");
                    }
                    request.setStatus(RequestStatus.CONFIRMED);
                }
                break;
        }
        return requestMapper.toEventRequestStatusUpdateResult(requestRepository.saveAll(requests));
    }


    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequestsInEvent(int userId, int eventId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Data not found");
        }
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Data not found");
        }
        return requestRepository.getRequestsByEventId(eventId).stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }
}