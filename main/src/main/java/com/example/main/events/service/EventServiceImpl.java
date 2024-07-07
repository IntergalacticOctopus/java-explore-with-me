package com.example.main.events.service;

import com.example.main.category.model.Category;
import com.example.main.category.repository.CategoryRepository;
import com.example.main.events.dto.*;
import com.example.main.events.mapper.EventMapper;
import com.example.main.events.model.Event;
import com.example.main.events.model.EventState;
import com.example.main.events.model.ModeratorEventState;
import com.example.main.events.repository.EventRepository;
import com.example.main.exception.errors.DataConflictException;
import com.example.main.exception.errors.NotFoundException;
import com.example.main.exception.errors.InvalidRequestException;
import com.example.main.request.dto.ParticipationRequestDto;
import com.example.main.request.mapper.RequestMapper;
import com.example.main.request.model.RequestStatus;
import com.example.main.request.repository.RequestRepository;
import com.example.main.user.model.User;
import com.example.main.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatDto;
import ru.practicum.client.StatClientImpl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final EventMapper eventMapper;
    private final StatClientImpl statClient;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;

    @Override
    @Transactional
    public EventFullDto postEvent(NewEventDto newEventDto, int userId) {
        if (LocalDateTime.now().plusHours(2).isAfter(newEventDto.getEventDate())) {
            throw new InvalidRequestException("Invalid date");
        }
        final User userFromDb = userRepository.findById(userId)
                .orElseThrow(
                        () -> new NotFoundException("Data not found")
                );
        final Category categoryFromDb = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(
                        () -> new NotFoundException("Data not found")
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
            throw new NotFoundException("Data not found");
        }
        return eventRepository.getByInitiatorId(userId, pageRequest).stream()
                .map(
                        event -> eventMapper.toEventShortDto(event)
                )
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventById(int userId, int eventId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Data not found");
        }
        final Event eventFromDb = eventRepository.findById(eventId)
                .orElseThrow(
                        () -> new NotFoundException("Data not found")
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
            throw new NotFoundException("Data not found");
        }
        final Event eventFromDb = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Data not found"));
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
                            () -> new NotFoundException("Data not found")
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
            throw new NotFoundException("Data not found");
        }
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Data not found");
        }
        return requestRepository.getRequestsByEventId(eventId).stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEvents(
            String text,
            List<Integer> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean onlyAvailable,
            String sort,
            PageRequest page) {
        if (text.isBlank()) {
            text = "";
        }
        if (rangeStart == null && rangeEnd == null) {
            rangeStart = LocalDateTime.now();
            rangeEnd = rangeStart.plusYears(1000);
        }
        if (rangeStart == null || rangeEnd == null || rangeStart.isAfter(rangeEnd)) {
            throw new InvalidRequestException("Invalid time");
        }
        final Page<Event> events;
        if (categories != null && categories.isEmpty()) {
            categories = null;
        }
        if (!paid) {
            if (onlyAvailable) {
                events = eventRepository.getBySearchAvailable(
                        EventState.PUBLISHED, text, categories,
                        rangeStart, rangeEnd, sort, page
                );
            } else {
                events = eventRepository.getBySearch(
                        EventState.PUBLISHED, text, categories,
                        rangeStart, rangeEnd, sort, page
                );
            }
        } else {
            if (onlyAvailable) {
                events = eventRepository.getBySearchAndPaidAvailable(
                        EventState.PUBLISHED, text, categories,
                        rangeStart, rangeEnd, true, sort, page
                );
            } else {
                events = eventRepository.getBySearchAndPaid(
                        EventState.PUBLISHED, text, categories,
                        rangeStart, rangeEnd, true, sort, page
                );
            }
        }
        return events.getContent().stream()
                .map(
                        event -> eventMapper.toEventShortDto(event)
                )
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventById(int id) {
        final Event eventFromDb = eventRepository.getByIdAndState(id, EventState.PUBLISHED)
                .orElseThrow(
                        () -> new NotFoundException("Data not found")
                );
        final List<String> uris = List.of("/events/" + id);
        final List<StatDto> statDto = statClient.getStats(
                LocalDateTime.now(),
                LocalDateTime.now(),
                uris,
                true
        );
        eventFromDb.setViews(Math.toIntExact(statDto.get(0).getHits()));
        return eventMapper.toEventFullDto(
                eventFromDb,
                requestRepository.countRequestByEventIdAndStatus(eventFromDb.getId(), RequestStatus.CONFIRMED)
        );

    }

    @Override
    @Transactional
    public EventFullDto patchEvent(UpdateEventAdminRequest updateEventAdminRequest, int eventId) {
        final Event eventFromDb = eventRepository.findById(eventId)
                .orElseThrow(
                        () -> new NotFoundException("Data not found")
                );
        if (updateEventAdminRequest.getStateAction() != null) {
            ModeratorEventState action = ModeratorEventState.valueOf(updateEventAdminRequest.getStateAction());
            switch (action) {
                case PUBLISH_EVENT:
                    if (eventFromDb.getState() != EventState.PENDING) {
                        throw new DataConflictException("Wrong state: PENDING");
                    }
                    if (LocalDateTime.now().plusHours(1).isAfter(eventFromDb.getEventDate())) {
                        throw new DataConflictException("Invalid data");
                    }
                    eventFromDb.setState(EventState.PUBLISHED);
                    eventFromDb.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    if (eventFromDb.getState() != EventState.PENDING) {
                        throw new DataConflictException("Wrong state: PENDING");
                    }
                    eventFromDb.setState(EventState.CANCELED);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid action");
            }
        }
        if (updateEventAdminRequest.getEventDate() != null) {
            eventFromDb.setEventDate(LocalDateTime.parse(updateEventAdminRequest.getEventDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if (updateEventAdminRequest.getAnnotation() != null) {
            eventFromDb.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (updateEventAdminRequest.getCategory() != null &&
                updateEventAdminRequest.getCategory() != eventFromDb.getCategory().getId()) {
            Category category = categoryRepository.findById(updateEventAdminRequest.getCategory())
                    .orElseThrow(
                            () -> new NotFoundException("Data not found")
                    );
            eventFromDb.setCategory(category);
        }
        if (updateEventAdminRequest.getDescription() != null) {
            eventFromDb.setDescription(updateEventAdminRequest.getDescription());
        }
        if (updateEventAdminRequest.getLocation() != null) {
            eventFromDb.setLocation(updateEventAdminRequest.getLocation());
        }
        if (updateEventAdminRequest.getPaid() != null) {
            eventFromDb.setPaid(updateEventAdminRequest.getPaid());
        }
        if (updateEventAdminRequest.getRequestModeration() != null) {
            eventFromDb.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }
        if (updateEventAdminRequest.getTitle() != null) {
            eventFromDb.setTitle(updateEventAdminRequest.getTitle());
        }
        if (updateEventAdminRequest.getParticipantLimit() != null) {
            eventFromDb.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }
        return eventMapper.toEventFullDto(
                eventRepository.save(eventFromDb),
                requestRepository.countRequestByEventIdAndStatus(eventFromDb.getId(), RequestStatus.CONFIRMED)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getEvents(List<Integer> usersIds,
                                        List<String> states,
                                        List<Integer> categories,
                                        LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd,
                                        PageRequest pageRequest) {
        List<EventState> eventStates = null;
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
            rangeEnd = rangeStart.plusYears(1000);
        }
        if (rangeEnd.isBefore(rangeStart)) {
            throw new InvalidRequestException("Invalid time");
        }
        if (usersIds != null && usersIds.isEmpty()) {
            usersIds = null;
        }
        if (states != null && states.isEmpty()) {
            states = null;
        }
        if (categories != null && categories.isEmpty()) {
            categories = null;
        }
        if (states != null) {
            eventStates = new ArrayList<>();
            for (String state : states) {
                if (!EnumUtils.isValidEnum(EventState.class, state)) {
                    throw new InvalidRequestException("Invalid state");
                }
                eventStates.add(EventState.valueOf(state));
            }
        }

        final Page<Event> events = eventRepository.getByUserIdsStatesCategories(
                usersIds, eventStates, categories,
                rangeStart, rangeEnd, pageRequest
        );
        return events.getContent().stream()
                .map(
                        event -> eventMapper.toEventFullDto(
                                event,
                                requestRepository.countRequestByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED)
                        )
                )
                .collect(Collectors.toList());
    }
}
