package com.example.main.compilation.mapper;

import com.example.main.compilation.dto.CompilationDto;
import com.example.main.compilation.dto.NewCompilationDto;
import com.example.main.compilation.model.Compilation;
import com.example.main.events.dto.EventShortDto;
import com.example.main.events.mapper.EventMapper;
import com.example.main.events.repository.EventRepository;
import com.example.main.request.model.RequestStatus;
import com.example.main.request.repository.RequestRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class CompilationMapper {
    private final EventMapper eventMapper;
    private final EventRepository eventRepository;
    public Compilation toCompilation(NewCompilationDto newCompilationDto) {
        return Compilation.builder()
                .title(newCompilationDto.getTitle())
                .pinned(newCompilationDto.getPinned() != null)
                .build();

    }

    public CompilationDto toCompilationDto(Compilation compilation,
                                           List<EventShortDto> eventsList) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .events(eventsList)
                .build();
    }

    public CompilationDto toCompilationDto(Compilation compilation,
                                           RequestRepository requestRepository) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .events(compilation.getEvents().stream()
                        .map(event -> eventMapper.toEventShortDto(
                                        event,
                                        requestRepository.countRequestByEventIdAndStatus(
                                                event.getId(),
                                                RequestStatus.CONFIRMED
                                        )
                                )
                        )
                        .collect(Collectors.toList()))
                .build();
    }
}