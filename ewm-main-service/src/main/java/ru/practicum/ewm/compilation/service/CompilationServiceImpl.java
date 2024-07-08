package ru.practicum.ewm.compilation.service;

import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.exception.errors.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final EventRepository eventRepository;
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventMapper eventMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilation(boolean pinned, PageRequest pageRequest) {
        if (pinned) {
            return compilationRepository.getAllByPinned(true, pageRequest).getContent().stream()
                    .map(event -> compilationMapper.toCompilationDto(event))
                    .collect(Collectors.toList());
        }
        return compilationRepository.findAll(pageRequest).getContent().stream()
                .map(event -> compilationMapper.toCompilationDto(event))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilationById(int compilationId) {
        final Compilation compilation = compilationRepository.findById(compilationId)
                .orElseThrow(
                        () -> new NotFoundException("Data not found")
                );
        return compilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public CompilationDto postCompilation(NewCompilationDto compilationDto) {

        Set<Event> events = new HashSet<>();
        if (compilationDto.getEvents() != null) {
            events = new HashSet<>(eventRepository.findAllById(compilationDto.getEvents()));
            if (events.size() != compilationDto.getEvents().size()) {
                throw new NotFoundException("Number of events does not match");
            }
        }
        Compilation compilation = compilationMapper.toCompilation(compilationDto);

        compilation.setEvents(events);

        final Compilation compilationFromDb = compilationRepository.save(compilation);

        List<EventShortDto> eventShortDtoList = events.stream()
                .map(
                        event -> eventMapper.toEventShortDto(event)
                )
                .collect(Collectors.toList());

        return compilationMapper.toCompilationDto(compilationFromDb, eventShortDtoList);

    }

    @Override
    @Transactional
    public void deleteCompilation(int compilationId) {
        if (!compilationRepository.existsById(compilationId)) {
            throw new NotFoundException("Data not found");
        }
        compilationRepository.deleteById(compilationId);
    }

    @Override
    @Transactional
    public CompilationDto patchCompilation(UpdateCompilationRequest updateCompilationRequest, int compilationId) {
        final Compilation compilationFromDb = compilationRepository.findById(compilationId)
                .orElseThrow(
                        () -> new NotFoundException("Data not found")
                );
        if (updateCompilationRequest.getTitle() != null && !updateCompilationRequest.getTitle().isBlank()) {
            compilationFromDb.setTitle(updateCompilationRequest.getTitle());
        }
        if (updateCompilationRequest.getPinned() != null) {
            compilationFromDb.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getEvents() != null) {
            if (updateCompilationRequest.getEvents().isEmpty()) {
                compilationFromDb.setEvents(new HashSet<>());
            } else {
                Set<Event> events = new HashSet<>(
                        eventRepository.findAllById(updateCompilationRequest.getEvents()));
                if (events.size() != updateCompilationRequest.getEvents().size()) {
                    throw new NotFoundException("Number of events does not match");
                }
                compilationFromDb.setEvents(events);
            }
        }
        return compilationMapper.toCompilationDto(
                compilationRepository.save(compilationFromDb)
        );
    }
}
