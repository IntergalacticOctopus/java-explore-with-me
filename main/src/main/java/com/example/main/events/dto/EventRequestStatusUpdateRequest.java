package com.example.main.events.dto;

import com.example.main.request.model.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class EventRequestStatusUpdateRequest {
    private Set<Integer> requestIds;

    private RequestStatus status;

}