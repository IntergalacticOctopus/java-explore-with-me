package com.example.main.compilation.controller;

import com.example.main.compilation.dto.CompilationDto;
import com.example.main.compilation.dto.NewCompilationDto;
import com.example.main.compilation.dto.UpdateCompilationRequest;
import com.example.main.compilation.service.AdminCompilationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
@Validated
public class AdminCompilationController {
    private final AdminCompilationService adminCompilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto post(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        return adminCompilationService.postCompilation(newCompilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @PositiveOrZero int compId) {
        adminCompilationService.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto patch(@RequestBody @Valid UpdateCompilationRequest updateCompilationRequest,
                                @PathVariable @PositiveOrZero int compId) {
        return adminCompilationService.patchCompilation(updateCompilationRequest, compId);
    }
}