package ru.practicum.ewm;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class StatDto {
    private String app;

    private String uri;

    private Long hits;
}