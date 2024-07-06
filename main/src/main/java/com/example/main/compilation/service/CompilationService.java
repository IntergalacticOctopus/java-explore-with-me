package com.example.main.compilation.service;

import com.example.main.compilation.dto.CompilationDto;
import com.example.main.compilation.dto.NewCompilationDto;
import com.example.main.compilation.dto.UpdateCompilationRequest;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto postCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilation(int compilationId);

    CompilationDto patchCompilation(UpdateCompilationRequest updateCompilationRequest, int compilationId);

    List<CompilationDto> getCompilation(boolean pinned, PageRequest pageRequest);

    CompilationDto getCompilationById(int compId);
}
