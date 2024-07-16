package ru.practicum.ewm.events.service;

import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.client.StatClient;
import ru.practicum.ewm.events.dto.*;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.EventState;
import ru.practicum.ewm.events.model.ModeratorEventState;
import ru.practicum.ewm.events.dto.PublicEventParamDto;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.exception.errors.DataConflictException;
import ru.practicum.ewm.exception.errors.NotFoundException;
import ru.practicum.ewm.exception.errors.InvalidRequestException;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.ewm.request.model.RequestStatus.CONFIRMED;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final EventMapper eventMapper;
    private final StatClient statClient;
    private final UserRepository userRepository;
    private static final LocalDateTime DATE_TIME = LocalDateTime.of(2000, 1, 1, 0, 0);

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
                requestRepository.countRequestByEventIdAndStatus(eventFromDb.getId(), CONFIRMED),
                0
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
        List<Event> events = List.of(eventFromDb);
        Map<Integer, Integer> confirmedRequests = getConfirmedRequests(events);
        Map<Integer, Integer> viewStats = getViews(events);

        return eventMapper.toEventFullDto(
                eventFromDb,
                confirmedRequests.getOrDefault(eventId, 0),
                viewStats.getOrDefault(eventId, 0)
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
        List<Event> events = List.of(eventFromDb);
        Map<Integer, Integer> confirmedRequests = getConfirmedRequests(events);
        Map<Integer, Integer> viewStats = getViews(events);
        return eventMapper.toEventFullDto(
                eventRepository.save(eventFromDb),
                confirmedRequests.getOrDefault(eventFromDb.getId(), 0),
                viewStats.getOrDefault(eventFromDb.getId(), 0));
    }


    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getShortEvents(PublicEventParamDto publicEventParamDto,
                                              PageRequest page) {

        String text = publicEventParamDto.getText();
        List<Integer> categories = publicEventParamDto.getCategories();
        Boolean paid = publicEventParamDto.getPaid();
        LocalDateTime rangeStart = publicEventParamDto.getRangeStart();
        LocalDateTime rangeEnd = publicEventParamDto.getRangeEnd();
        Boolean onlyAvailable = publicEventParamDto.getOnlyAvailable();
        String sort = publicEventParamDto.getSort();


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

        List<Event> events = List.of(eventFromDb);
        Map<Integer, Integer> confirmedRequests = getConfirmedRequests(events);
        Map<Integer, Integer> viewStats = getViews(events);

        return eventMapper.toEventFullDto(
                eventFromDb,
                confirmedRequests.getOrDefault(eventFromDb.getId(), 0),
                viewStats.getOrDefault(eventFromDb.getId(), 0));
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
        List<Event> events = List.of(eventFromDb);
        Map<Integer, Integer> confirmedRequests = getConfirmedRequests(events);
        Map<Integer, Integer> viewStats = getViews(events);
        return eventMapper.toEventFullDto(
                eventRepository.save(eventFromDb),
                confirmedRequests.getOrDefault(eventFromDb.getId(), 0),
                viewStats.getOrDefault(eventFromDb.getId(), 0));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getFullEvents(PublicEventParamDto publicEventParamDto,
                                            PageRequest pageRequest) {

        List<Integer> usersIds = publicEventParamDto.getUsersIds();
        List<String> states = publicEventParamDto.getState();
        List<Integer> categories = publicEventParamDto.getCategories();
        LocalDateTime rangeStart = publicEventParamDto.getRangeStart();
        LocalDateTime rangeEnd = publicEventParamDto.getRangeEnd();

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

        final List<Event> events = eventRepository.getByUserIdsStatesCategories(
                usersIds, eventStates, categories,
                rangeStart, rangeEnd, pageRequest
        );
        Map<Integer, Integer> confirmedRequests = getConfirmedRequests(events);
        Map<Integer, Integer> viewStats = getViews(events);
        return events.stream()
                .map(event -> eventMapper.toEventFullDto(
                        event,
                        confirmedRequests.getOrDefault(event.getId(), 0),
                        viewStats.getOrDefault(event.getId(), 0)))
                .collect(Collectors.toList());
    }

    private Map<Integer, Integer> getConfirmedRequests(List<Event> events) {
        if (events.isEmpty()) return Collections.emptyMap();
        List<Integer> idList = events.stream().map(Event::getId).collect(Collectors.toList());
        List<RequestCounterDto> resultList = requestRepository.findByStatus(idList, CONFIRMED);
        return resultList.stream().collect(Collectors.toMap(RequestCounterDto::getEventId, RequestCounterDto::getCount));
    }

    private Map<Integer, Integer> getViews(List<Event> events) {
        if (events.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, Integer> eventUrisAndIds = new HashMap<>();
        for (Event event : events) {
            String key = "/events/" + event.getId();
            eventUrisAndIds.put(key, event.getId());
        }
        List<StatDto> stats = statClient.getStats(
                DATE_TIME, LocalDateTime.now(), List.copyOf(eventUrisAndIds.keySet()), true);
        Map<Integer, Integer> result = new HashMap<>();
        for (StatDto stat : stats) {
            if (eventUrisAndIds.containsKey(stat.getUri())) {
                Integer eventId = eventUrisAndIds.get(stat.getUri());
                result.put(eventId, Math.toIntExact(stat.getHits()));
            }
        }
        return result;
    }
}
