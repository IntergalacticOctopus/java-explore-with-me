package com.example.main.compilation.service;

import com.example.main.compilation.dto.CompilationDto;
import com.example.main.compilation.mapper.CompilationMapper;
import com.example.main.compilation.model.Compilation;
import com.example.main.compilation.repository.CompilationRepository;
import com.example.main.exception.model.EntityNotFoundException;
import com.example.main.request.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PublicCompilationServiceImpl implements PublicCompilationService {
    private final CompilationRepository compilationRepository;
    private final RequestRepository requestRepository;
    private final CompilationMapper compilationMapper;

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
}