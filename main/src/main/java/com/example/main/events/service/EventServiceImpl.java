package com.example.main.events.service;

import com.example.main.category.model.Category;
import com.example.main.category.repository.CategoryRepository;
import com.example.main.events.dto.EventFullDto;
import com.example.main.events.dto.EventShortDto;
import com.example.main.events.dto.UpdateEventAdminRequest;
import com.example.main.events.enums.SortConflict;
import com.example.main.events.mapper.EventMapper;
import com.example.main.events.model.Event;
import com.example.main.events.model.EventState;
import com.example.main.events.model.ModeratorEventState;
import com.example.main.events.repository.EventRepository;
import com.example.main.exception.model.DataConflictException;
import com.example.main.exception.model.EntityNotFoundException;
import com.example.main.exception.model.InvalidRequestException;
import com.example.main.request.model.RequestStatus;
import com.example.main.request.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final EventMapper eventMapper;
    private final StatClient statClient;

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
                        event -> eventMapper.toEventShortDto(
                                event,
                                requestRepository.countRequestByEventIdAndStatus(
                                        event.getId(),
                                        RequestStatus.CONFIRMED)
                        )
                )
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventById(int id) {
        final Event eventFromDb = eventRepository.getByIdAndState(id, EventState.PUBLISHED)
                .orElseThrow(
                        () -> new EntityNotFoundException("Data not found")
                );
        final List<String> uris = List.of("/events/" + id);
        final ResponseEntity<Object> responseEntity = (ResponseEntity<Object>) statClient.getStats(
                LocalDateTime.now(),
                LocalDateTime.now(),
                uris,
                true
        );
        int hits = 0;
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            try {
                ArrayList<Map> hitsMap = (ArrayList<Map>) responseEntity.getBody();
                hits = Integer.parseInt(String.valueOf(hitsMap.get(0).getOrDefault("hits", "0")));
            } catch (NumberFormatException e) {
                throw new RuntimeException(e);
            }
        }
        eventFromDb.setViews(hits);
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
                        () -> new EntityNotFoundException("Data not found")
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
                            () -> new EntityNotFoundException("Data not found")
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
