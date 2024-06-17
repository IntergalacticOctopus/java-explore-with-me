package com.example.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.practicum.HitDto;
import ru.practicum.StatDto;
import ru.practicum.client.StatClient;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
public class MainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
        StatClient client = new StatClient("http://localhost:9090");
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime start = now.minusDays(1);
        LocalDateTime end = now.plusDays(1);

        HitDto firstHit = new HitDto("firstApp", "/firstUrl", "firstIp", LocalDateTime.now());
        System.out.println(firstHit);

        HitDto secondHit = new HitDto("secondApp", "/secondUrl", "secondIp", LocalDateTime.now().plusMinutes(3));
        System.out.println(secondHit);

        client.saveHit(secondHit);
        client.saveHit(firstHit);

        List<StatDto> firstStats = client.getStats(start, end, false, List.of("/firstUrl"));
        System.out.println(firstStats);

        List<StatDto> secondStats = client.getStats(start, end, false, List.of("secondUrl"));
        System.out.println(secondStats);

    }

}
