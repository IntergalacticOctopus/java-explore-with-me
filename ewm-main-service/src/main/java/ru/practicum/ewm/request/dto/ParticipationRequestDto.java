package ru.practicum.ewm.request.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ParticipationRequestDto {
    private int id;

    private int event;

    private String created;

    private int requester;

    private String status;
}