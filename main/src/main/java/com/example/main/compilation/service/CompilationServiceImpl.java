package com.example.main.compilation.service;

import com.example.main.compilation.dto.CompilationDto;
import com.example.main.compilation.dto.NewCompilationDto;
import com.example.main.compilation.dto.UpdateCompilationRequest;
import com.example.main.compilation.mapper.CompilationMapper;
import com.example.main.compilation.model.Compilation;
import com.example.main.compilation.repository.CompilationRepository;
import com.example.main.events.dto.EventShortDto;
import com.example.main.events.mapper.EventMapper;
import com.example.main.events.model.Event;
import com.example.main.events.repository.EventRepository;
import com.example.main.exception.errors.DataConflictException;
import com.example.main.exception.errors.NotFoundException;
import com.example.main.request.model.RequestStatus;
import com.example.main.request.repository.RequestRepository;
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
    private final RequestRepository requestRepository;
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventMapper eventMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilation(boolean pinned, PageRequest pageRequest) {
        if (pinned) {
            return compilationRepository.getAllByPinned(true, pageRequest).getContent().stream()
                    .map(event -> compilationMapper.toCompilationDto(event, requestRepository))
                    .collect(Collectors.toList());
        }
        return compilationRepository.findAll(pageRequest).getContent().stream()
                .map(event -> compilationMapper.toCompilationDto(event, requestRepository))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilationById(int compilationId) {
        final Compilation compilation = compilationRepository.findById(compilationId)
                .orElseThrow(
                        () -> new NotFoundException("Data not found")
                );
        return compilationMapper.toCompilationDto(compilation, requestRepository);
    }

    @Override
    @Transactional
    public CompilationDto postCompilation(NewCompilationDto compilationDto) {


        if (compilationRepository.existsByTitle(compilationDto.getTitle())) {
            throw new DataConflictException("Data not found");
        }

        Set<Event> events = new HashSet<>();
        if (compilationDto.getEvents() != null) {
            events = new HashSet<>(eventRepository.findAllById(compilationDto.getEvents()));
        }
        Compilation compilation = compilationMapper.toCompilation(compilationDto);
        compilation.setEvents(events);

        final Compilation compilationFromDb = compilationRepository.save(compilation);

        List<EventShortDto> eventShortDtoList = events.stream()
                .map(
                        event -> eventMapper.toEventShortDto(
                                event,
                                requestRepository.countRequestByEventIdAndStatus(
                                        event.getId(),
                                        RequestStatus.CONFIRMED
                                )
                        )
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
                compilationFromDb.setEvents(new HashSet<>(
                        eventRepository.findAllById(updateCompilationRequest.getEvents()))
                );
            }
        }
        return compilationMapper.toCompilationDto(
                compilationRepository.save(compilationFromDb),
                requestRepository
        );
    }
}
