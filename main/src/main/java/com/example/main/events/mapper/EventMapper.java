package com.example.main.events.mapper;

import com.example.main.category.mapper.CategoryMapper;
import com.example.main.category.model.Category;
import com.example.main.events.dto.EventFullDto;
import com.example.main.events.dto.EventShortDto;
import com.example.main.events.dto.NewEventDto;
import com.example.main.events.model.Event;
import com.example.main.events.model.EventState;
import com.example.main.user.mapper.UserMapper;
import com.example.main.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class EventMapper {
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;

    public Event toEvent(NewEventDto newEventDto, User initiator, Category category) {
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .category(category)
                .createdOn(LocalDateTime.now())
                .eventDate(newEventDto.getEventDate())
                .initiator(initiator)
                .location(newEventDto.getLocation())
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration())
                .state(EventState.PENDING)
                .title(newEventDto.getTitle())
                .build();

    }

    public EventShortDto toEventShortDto(Event event, int confirmedRequests) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(categoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(confirmedRequests)
                .eventDate(event.getEventDate())
                .initiator(userMapper.createUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public EventFullDto toEventFullDto(Event event, int confirmedRequests) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .description(event.getDescription())
                .category(categoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(confirmedRequests)
                .createdOn(event.getCreatedOn())
                .eventDate(event.getEventDate())
                .initiator(userMapper.createUserShortDto(event.getInitiator()))
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .publishedOn(event.getPublishedOn())
                .build();
    }
}