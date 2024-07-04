package com.example.main.request.controller;

import com.example.main.request.dto.ParticipationRequestDto;
import com.example.main.request.service.PrivateRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Validated
public class PrivateRequestControllerImpl {
    private final PrivateRequestService privateRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto post(@PathVariable @PositiveOrZero int userId,
                                        @RequestParam @PositiveOrZero int eventId) {
        return privateRequestService.postRequest(userId, eventId);
    }

    @GetMapping
    public List<ParticipationRequestDto> get(@PathVariable @PositiveOrZero int userId) {
        return privateRequestService.getRequests(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto patch(@PathVariable @PositiveOrZero int userId,
                                         @PathVariable @PositiveOrZero int requestId) {
        return privateRequestService.cancelRequest(userId, requestId);
    }
}