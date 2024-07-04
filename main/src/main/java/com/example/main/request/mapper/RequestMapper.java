package com.example.main.request.mapper;

import com.example.main.events.dto.EventRequestStatusUpdateResult;
import com.example.main.events.model.Event;
import com.example.main.request.dto.ParticipationRequestDto;
import com.example.main.request.model.Request;
import com.example.main.request.model.RequestStatus;
import com.example.main.user.model.User;
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