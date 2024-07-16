package ru.practicum.ewm.request.mapper;

import ru.practicum.ewm.events.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class RequestMapper {
    public Request toRequest(User user, Event event) {
        Request request = Request.builder()
                .requester(user)
                .created(LocalDateTime.now())
                .event(event)
                .build();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }

        return request;
    }

    public ParticipationRequestDto toParticipationRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .event(request.getEvent().getId())
                .created(request.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .requester(request.getRequester().getId())
                .status(request.getStatus().toString())
                .build();
    }

    public EventRequestStatusUpdateResult toEventRequestStatusUpdateResult(List<Request> requests) {
        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(requests.stream().filter(request -> request.getStatus() == RequestStatus.CONFIRMED).map(this::toParticipationRequestDto).collect(Collectors.toList()))
                .rejectedRequests(requests.stream().filter(request -> request.getStatus() == RequestStatus.REJECTED).map(this::toParticipationRequestDto).collect(Collectors.toList()))
                .build();
    }
}