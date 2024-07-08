package ru.practicum.ewm.client;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.HitDto;
import ru.practicum.ewm.StatDto;

import java.time.LocalDateTime;
import java.util.List;

@Component
public interface StatClient {
    void saveHit(HitDto hitDto);

    List<StatDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
