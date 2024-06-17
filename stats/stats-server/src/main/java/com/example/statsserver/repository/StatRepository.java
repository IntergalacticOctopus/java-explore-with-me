package com.example.statsserver.repository;

import com.example.statsserver.model.Hit;
import com.example.statsserver.model.Stat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<Hit, Long> {

    @Query("SELECT new com.example.statsserver.model.Stat(h.app, h.uri, count(h.ipAddress)) " +
            "FROM Hit AS h WHERE h.hitDate " +
            "BETWEEN :start AND :end AND (COALESCE(:uris, null) IS NULL OR h.uri IN :uris) " +
            "GROUP BY h.app, h.uri ORDER BY COUNT(h.ipAddress) DESC")
    List<Stat> getStats(@Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end,
                        @Param("uris") List<String> uris);

    @Query("SELECT new com.example.statsserver.model.Stat(h.app, h.uri, count(DISTINCT h.ipAddress)) " +
            "FROM Hit AS h WHERE h.hitDate BETWEEN :start AND :end AND (:uris IS NULL OR h.uri IN :uris) " +
            "GROUP BY h.app, h.uri ORDER BY COUNT(DISTINCT h.ipAddress) DESC")
    List<Stat> getUniqueStats(@Param("start") LocalDateTime start,
                              @Param("end") LocalDateTime end,
                              @Param("uris") List<String> uris);
}