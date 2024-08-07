package ru.practicum.ewm.compilation.dto;

import ru.practicum.ewm.events.dto.EventShortDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class CompilationDto {
    private int id;

    private Boolean pinned;

    private String title;

    private List<EventShortDto> events;
}