package com.example.main.events.service;

import com.example.main.category.model.Category;
import com.example.main.category.repository.CategoryRepository;
import com.example.main.events.dto.*;
import com.example.main.events.mapper.EventMapper;
import com.example.main.events.model.Event;
import com.example.main.events.model.EventState;
import com.example.main.events.repository.EventRepository;
import com.example.main.exception.model.DataConflictException;
import com.example.main.exception.model.EntityNotFoundException;
import com.example.main.exception.model.InvalidRequestException;
import com.example.main.request.dto.ParticipationRequestDto;
import com.example.main.request.mapper.RequestMapper;
import com.example.main.request.model.Request;
import com.example.main.request.model.RequestStatus;
import com.example.main.request.repository.RequestRepository;
import com.example.main.user.model.User;
import com.example.main.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrivateEventServiceImpl implements PrivateEventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final EventMapper eventMapper;
    private final RequestMapper requestMapper;

    @Override
    @Transactional
    public EventFullDto postEvent(NewEventDto newEventDto, int userId) {
        if (LocalDateTime.now().plusHours(2).isAfter(newEventDto.getEventDate())) {
            throw new InvalidRequestException("Invalid date");
        }
        final User userFromDb = userRepository.findById(userId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Data not found")
                );
        final Category categoryFromDb = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(
                        () -> new EntityNotFoundException("Data not found")
                );
        final Event event = eventMapper.toEvent(newEventDto, userFromDb, categoryFromDb);
        final Event eventFromDb = eventRepository.save(event);
        return eventMapper.toEventFullDto(
                eventFromDb,
                requestRepository.countRequestByEventIdAndStatus(eventFromDb.getId(), RequestStatus.CONFIRMED)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getUserEvents(int userId, PageRequest pageRequest) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Data not found");
        }
        return eventRepository.getByInitiatorId(userId, pageRequest).stream()
                .map(
                        event -> eventMapper.toEventShortDto(
                                event,
                                requestRepository.countRequestByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED))
                )
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventById(int userId, int eventId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Data not found");
        }
        final Event eventFromDb = eventRepository.findById(eventId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Data not found")
                );
        return eventMapper.toEventFullDto(
                eventFromDb,
                requestRepository.countRequestByEventIdAndStatus(eventFromDb.getId(), RequestStatus.CONFIRMED)
        );
    }

    @Override
    @Transactional
    public EventFullDto patchEvent(UpdateEventUserRequest updateEventUserRequest, int userId, int eventId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Data not found");
        }
        final Event eventFromDb = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Data not found"));
        if (eventFromDb.getState().equals(EventState.PUBLISHED)) {
            throw new DataConflictException("Invalid data");
        }
        if (updateEventUserRequest.getStateAction() != null) {
            switch (updateEventUserRequest.getStateAction()) {
                case CANCEL_REVIEW:
                    eventFromDb.setState(EventState.CANCELED);
                    break;
                case SEND_TO_REVIEW:
                    eventFromDb.setState(EventState.PENDING);
                    break;
                default:
                    throw new InvalidRequestException("Invalid action");
            }
        }
        if (updateEventUserRequest.getEventDate() != null) {
            eventFromDb.setEventDate(updateEventUserRequest.getEventDate());
        }
        if (updateEventUserRequest.getAnnotation() != null) {
            eventFromDb.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (updateEventUserRequest.getCategory() != null &&
                updateEventUserRequest.getCategory() != (eventFromDb.getCategory().getId())) {
            Category category = categoryRepository.findById(updateEventUserRequest.getCategory())
                    .orElseThrow(
                            () -> new EntityNotFoundException("Data not found")
                    );
            eventFromDb.setCategory(category);
        }
        if (updateEventUserRequest.getDescription() != null) {
            eventFromDb.setDescription(updateEventUserRequest.getDescription());
        }
        if (updateEventUserRequest.getLocation() != null) {
            eventFromDb.setLocation(updateEventUserRequest.getLocation());
        }
        if (updateEventUserRequest.getPaid() != null) {
            eventFromDb.setPaid(updateEventUserRequest.getPaid());
        }
        if (updateEventUserRequest.getRequestModeration() != null) {
            eventFromDb.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }
        if (updateEventUserRequest.getTitle() != null) {
            eventFromDb.setTitle(updateEventUserRequest.getTitle());
        }
        if (updateEventUserRequest.getParticipantLimit() != null) {
            eventFromDb.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }
        return eventMapper.toEventFullDto(
                eventRepository.save(eventFromDb),
                requestRepository.countRequestByEventIdAndStatus(eventFromDb.getId(), RequestStatus.CONFIRMED)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequestsInEvent(int userId, int eventId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Data not found");
        }
        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("Data not found");
        }
        return requestRepository.getRequestsByEventId(eventId).stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult patchRequests(EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest,
                                                        int userId,
                                                        int eventId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("Data not found"));
        }
        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException(String.format("Data not found"));
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

}