package ru.practicum.ewm.compilation.service;

import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto postCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilation(int compilationId);

    CompilationDto patchCompilation(UpdateCompilationRequest updateCompilationRequest, int compilationId);

    List<CompilationDto> getCompilation(boolean pinned, PageRequest pageRequest);

    CompilationDto getCompilationById(int compId);
}
