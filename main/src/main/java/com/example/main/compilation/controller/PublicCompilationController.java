package com.example.main.compilation.controller;

import com.example.main.compilation.dto.CompilationDto;
import com.example.main.compilation.service.PublicCompilationService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Validated
public class PublicCompilationController {
    private final PublicCompilationService publicCompilationService;

    @GetMapping
    public List<CompilationDto> get(@RequestParam(defaultValue = "false") boolean pinned,
                                    @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                    @RequestParam(defaultValue = "10") @Positive int size) {
        return publicCompilationService.getCompilation(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getById(@PathVariable @PositiveOrZero int compId) {
        return publicCompilationService.getCompilationById(compId);
    }
}