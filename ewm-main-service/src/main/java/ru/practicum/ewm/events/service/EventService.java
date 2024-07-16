package ru.practicum.ewm.events.service;

import ru.practicum.ewm.events.dto.*;
import ru.practicum.ewm.events.dto.PublicEventParamDto;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface EventService {
    EventFullDto patchEvent(UpdateEventAdminRequest updateEventAdminRequest, int eventId);

    List<EventFullDto> getFullEvents(PublicEventParamDto publicEventParamDto, PageRequest pageRequest);

    List<EventShortDto> getShortEvents(PublicEventParamDto publicEventParamDto, PageRequest page);

    EventFullDto getEventById(int id);

    EventFullDto postEvent(NewEventDto newEventDto, int userId);

    List<EventShortDto> getUserEvents(int userId, PageRequest pageRequest);

    EventFullDto getEventById(int userId, int eventId);

    EventFullDto patchEvent(UpdateEventUserRequest updateEventUserRequest, int userId, int eventId);


}
