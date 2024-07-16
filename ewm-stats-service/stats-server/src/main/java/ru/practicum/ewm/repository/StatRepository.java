package ru.practicum.ewm.repository;

import ru.practicum.ewm.StatDto;
import ru.practicum.ewm.model.Hit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatRepository extends JpaRepository<Hit, Long> {
    @Query("select new ru.practicum.ewm.StatDto(h.app, h.uri, count(h.ip)) " +
            "from Hit as h " +
            "where h.timestamp between ?1 and ?2 and h.uri IN ?3 " +
            "group by h.app, h.uri  " +
            "order by count (h.ip) desc ")
    List<StatDto> getStat(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.ewm.StatDto(h.app, h.uri, count(distinct h.ip)) " +
            "from Hit as h " +
            "where h.timestamp between ?1 and ?2 and h.uri IN ?3 " +
            "group by h.app, h.uri  " +
            "order by count (distinct h.ip) desc ")
    List<StatDto> getStatUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.ewm.StatDto(h.app, h.uri, count (h)) " +
            "from Hit as h " +
            "where h.timestamp between ?1 and ?2 " +
            "group by h.app, h.uri " +
            "order by count (h) desc ")
    List<StatDto> getStatNoUris(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.ewm.StatDto(h.app, h.uri, count (distinct h.ip)) " +
            "from Hit as h " +
            "where h.timestamp between ?1 and ?2 " +
            "group by h.app, h.uri " +
            "order by count (distinct h.ip) desc ")
    List<StatDto> getStatNoUrisUniqueIp(LocalDateTime start, LocalDateTime end);
}