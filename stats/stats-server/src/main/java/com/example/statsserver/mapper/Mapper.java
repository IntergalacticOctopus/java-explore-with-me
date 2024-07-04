package com.example.statsserver.mapper;

import com.example.statsserver.model.Hit;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.HitDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@NoArgsConstructor
public class Mapper {
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";


    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATETIME_FORMAT);

    public HitDto toHitDto(Hit hit) {
        return HitDto.builder()
                .id(hit.getId())
                .app(hit.getApp())
                .uri(hit.getUri())
                .ip(hit.getIp_address())
                .timestamp(hit.getTimestamp().format(FORMATTER))
                .build();
    }

    public Hit toHit(HitDto hitDto) {
        return Hit.builder()
                .id(hitDto.getId())
                .app(hitDto.getApp())
                .uri(hitDto.getUri())
                .ip_address(hitDto.getIp())
                .timestamp(LocalDateTime.parse(hitDto.getTimestamp(), FORMATTER))
                .build();
    }
}