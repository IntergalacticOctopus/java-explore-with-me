package ru.practicum;

import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Builder
public class HitDto {
    private Long id;
    private String app;
    private String uri;
    private String ip;
    private String timestamp;
}