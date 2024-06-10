package com.example.statsserver.service;

import com.example.statsserver.mapper.Mapper;
import com.example.statsserver.repository.StatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.HitDto;
import ru.practicum.StatDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class StatServiceImpl implements StatService {
    private final StatRepository statRepository;
    private final Mapper mapper;

    @Override
    public void saveHit(HitDto hitDtoRequest) {
        statRepository.save(mapper.toHit(hitDtoRequest));
    }

    @Override
    public List<StatDto> getStatInfo(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (uris == null) {
            uris = List.of();
        }
        if (unique) {
            return (statRepository.getUniqueStats(start, end, uris)).stream()
                    .map(mapper::toStatDto)
                    .collect(Collectors.toList());
        } else {

            return (statRepository.getStats(start, end, uris)).stream()
                    .map(mapper::toStatDto)
                    .collect(Collectors.toList());
        }
    }
}