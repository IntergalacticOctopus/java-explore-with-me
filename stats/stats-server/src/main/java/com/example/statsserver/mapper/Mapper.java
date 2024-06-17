package com.example.statsserver.mapper;

import com.example.statsserver.model.Hit;
import com.example.statsserver.model.Stat;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.HitDto;
import ru.practicum.StatDto;

@Component
@NoArgsConstructor
public class Mapper {

    public Hit toHit(HitDto hitDto) {
        return Hit.builder()
                .app(hitDto.getApp())
                .uri(hitDto.getUri())
                .ipAddress(hitDto.getIp())
                .hitDate(hitDto.getTimestamp())
                .build();
    }

    public StatDto toStatDto(Stat stat) {
        return StatDto.builder()
                .app(stat.getApp())
                .uri(stat.getUri())
                .hits(stat.getHits())
                .build();
    }
}