package com.example.main.events.service;


import com.example.main.events.dto.EventFullDto;
import com.example.main.events.dto.UpdateEventAdminRequest;

import java.time.LocalDateTime;
import java.util.List;


public interface AdminEventService {
    EventFullDto patchEvent(UpdateEventAdminRequest updateEventAdminRequest, int eventId);

    List<EventFullDto> getEvents(List<Integer> users, List<String> states, List<Integer> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);
}