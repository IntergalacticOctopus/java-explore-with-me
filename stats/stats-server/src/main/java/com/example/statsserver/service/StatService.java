package com.example.statsserver.service;

import ru.practicum.HitDto;
import ru.practicum.StatDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {

    void saveHit(HitDto hitDtoRequest);

    List<StatDto> getStatInfo(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
