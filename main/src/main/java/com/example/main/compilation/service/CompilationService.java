package com.example.main.compilation.service;

import com.example.main.compilation.dto.CompilationDto;
import com.example.main.compilation.dto.NewCompilationDto;
import com.example.main.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto postCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilation(int compilationId);

    CompilationDto patchCompilation(UpdateCompilationRequest updateCompilationRequest, int compilationId);

    List<CompilationDto> getCompilation(boolean pinned, int from, int size);

    CompilationDto getCompilationById(int compId);
}
