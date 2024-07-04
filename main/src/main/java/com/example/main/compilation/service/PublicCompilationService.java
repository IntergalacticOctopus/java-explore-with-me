package com.example.main.compilation.service;


import com.example.main.compilation.dto.CompilationDto;

import java.util.List;

public interface PublicCompilationService {

    List<CompilationDto> getCompilation(boolean pinned, int from, int size);

    CompilationDto getCompilationById(int compId);
}