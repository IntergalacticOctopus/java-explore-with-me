package ru.practicum.ewm.events.dto;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestCounterDto {

    private Integer eventId;

    private Integer count;

    public RequestCounterDto(Integer eventId, long count) {
        this.eventId = eventId;
        this.count = Math.toIntExact(count);
    }
}