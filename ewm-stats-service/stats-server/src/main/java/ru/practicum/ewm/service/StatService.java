package ru.practicum.ewm.service;

import org.springframework.stereotype.Service;
import ru.practicum.ewm.HitDto;
import ru.practicum.ewm.StatDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface StatService {
    HitDto saveHit(HitDto hitDto);

    List<StatDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
