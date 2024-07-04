package com.example.main.events.service;

import com.example.main.events.dto.EventFullDto;
import com.example.main.events.dto.EventShortDto;

import java.time.LocalDateTime;
import java.util.List;

public interface PublicEventService {
    List<EventShortDto> getEvents(String text, List<Integer> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, int from, int size);

    EventFullDto getEventById(int id);
}