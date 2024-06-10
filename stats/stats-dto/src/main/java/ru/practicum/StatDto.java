package ru.practicum;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
public class StatDto {

    private String app;

    private String uri;

    private Long hits;
}