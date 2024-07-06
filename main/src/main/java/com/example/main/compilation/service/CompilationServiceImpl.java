package com.example.main.compilation.service;

import com.example.main.compilation.dto.CompilationDto;
import com.example.main.compilation.dto.NewCompilationDto;
import com.example.main.compilation.dto.UpdateCompilationRequest;
import com.example.main.compilation.mapper.CompilationMapper;
import com.example.main.compilation.model.Compilation;
import com.example.main.compilation.model.EventCompilationConnection;
import com.example.main.compilation.repository.CompilationRepository;
import com.example.main.compilation.repository.EventCompilationConnectionRepository;
import com.example.main.events.dto.EventShortDto;
import com.example.main.events.mapper.EventMapper;
import com.example.main.events.model.Event;
import com.example.main.events.repository.EventRepository;
import com.example.main.exception.model.DataConflictException;
import com.example.main.exception.model.EntityNotFoundException;
import com.example.main.request.model.RequestStatus;
import com.example.main.request.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final CompilationRepository compilationRepository;
    private final EventCompilationConnectionRepository eventCompilationConnectionRepository;
    private final CompilationMapper compilationMapper;
    private final EventMapper eventMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilation(boolean pinned, int from, int size) {
        final PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
        if (pinned) {
            return compilationRepository.getAllByPinned(true, pageRequest).getContent().stream()
                    .map(event -> compilationMapper.toCompilation(event, requestRepository))
                    .collect(Collectors.toList());
        }
        return compilationRepository.findAll(pageRequest).getContent().stream()
                .map(event -> compilationMapper.toCompilation(event, requestRepository))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilationById(int compilationId) {
        final Compilation compilation = compilationRepository.findById(compilationId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Data not found")
                );
        return compilationMapper.toCompilation(compilation, requestRepository);
    }

    @Override
    @Transactional
    public CompilationDto postCompilation(NewCompilationDto compilationDto) {
        if (compilationRepository.existsByTitle(compilationDto.getTitle())) {
            throw new DataConflictException("Data not found");
        }
        List<EventShortDto> eventShortDtoList = new ArrayList<>();
        final Compilation compilation = compilationMapper.toNewCompilationDto(compilationDto);
        final Compilation compilationFromDb = compilationRepository.save(compilation);
        if (compilationDto.getEvents() != null && !compilationDto.getEvents().isEmpty()) {
            List<Event> events = eventRepository.findAllById(new ArrayList<>(compilationDto.getEvents()));
            eventShortDtoList = events.stream()
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
            for (Integer eventId : compilationDto.getEvents()) {
                eventCompilationConnectionRepository.save(
                        new EventCompilationConnection(0, eventId, compilationFromDb.getId())
                );
            }
            return compilationMapper.toCompilation(compilation, eventShortDtoList);
        }
        return compilationMapper.toCompilation(compilationFromDb, eventShortDtoList);
    }

    @Override
    @Transactional
    public void deleteCompilation(int compilationId) {
        if (!compilationRepository.existsById(compilationId)) {
            throw new EntityNotFoundException("Data not found");
        }
        compilationRepository.deleteById(compilationId);
    }

    @Override
    @Transactional
    public CompilationDto patchCompilation(UpdateCompilationRequest updateCompilationRequest, int compilationId) {
        final Compilation compilationFromDb = compilationRepository.findById(compilationId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Data not found")
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
        return compilationMapper.toCompilation(
                compilationRepository.save(compilationFromDb),
                requestRepository
        );
    }
}
