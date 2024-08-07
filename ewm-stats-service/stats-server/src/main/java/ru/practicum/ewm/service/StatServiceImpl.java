package ru.practicum.ewm.service;

import ru.practicum.ewm.HitDto;
import ru.practicum.ewm.StatDto;
import ru.practicum.ewm.mapper.Mapper;
import ru.practicum.ewm.model.Hit;
import ru.practicum.ewm.repository.StatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class StatServiceImpl implements StatService {
    private final StatRepository statServiceRepository;
    private final Mapper mapper;

    @Transactional
    @Override
    public HitDto saveHit(HitDto hitDto) {
        Hit hit = mapper.toHit(hitDto);
        Hit savedHit = statServiceRepository.save(hit);
        return mapper.toHitDto(savedHit);
    }

    @Transactional(readOnly = true)
    @Override
    public List<StatDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (end.isBefore(start)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start must be before end");
        }
        if (unique) {
            if (uris.isEmpty()) {
                return statServiceRepository.getStatNoUrisUniqueIp(start, end);
            }
            return statServiceRepository.getStatUniqueIp(start, end, uris);
        }
        if (uris.isEmpty()) {
            return statServiceRepository.getStatNoUris(start, end);
        }
        return statServiceRepository.getStat(start, end, uris);
    }
}