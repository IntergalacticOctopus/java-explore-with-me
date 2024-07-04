package com.example.main.events.dto;

import com.example.main.request.model.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class EventRequestStatusUpdateRequest {
    private List<Integer> requestIds;

    private RequestStatus status;

}