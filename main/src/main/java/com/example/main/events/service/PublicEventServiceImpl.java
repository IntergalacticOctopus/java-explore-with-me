package com.example.main.events.service;


import com.example.main.events.dto.EventFullDto;
import com.example.main.events.dto.EventShortDto;
import com.example.main.events.mapper.EventMapper;
import com.example.main.events.model.Event;
import com.example.main.events.model.EventState;
import com.example.main.events.repository.EventRepository;
import com.example.main.exception.model.EntityNotFoundException;
import com.example.main.exception.model.InvalidRequestException;
import com.example.main.request.model.RequestStatus;
import com.example.main.request.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final StatClient statClient;
    private final EventMapper eventMapper;

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
            int from,
            int size) {
        if (text.isBlank()) {
            text = "";
        }
        if (!sort.equalsIgnoreCase("EVENT_DATE") && !sort.equalsIgnoreCase("VIEWS")) {
            throw new InvalidRequestException("Invalid sorting");
        }
        if (rangeStart == null && rangeEnd == null) {
            rangeStart = LocalDateTime.now();
            rangeEnd = rangeStart.plusYears(1000);
        }
        if (rangeStart == null || rangeEnd == null || rangeStart.isAfter(rangeEnd)) {
            throw new InvalidRequestException("Invalid time");
        }
        final PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
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
        final ResponseEntity<Object> responseEntity = statClient.getStats(
                LocalDateTime.now().minusYears(1000),
                LocalDateTime.now().plusYears(1000),
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
}