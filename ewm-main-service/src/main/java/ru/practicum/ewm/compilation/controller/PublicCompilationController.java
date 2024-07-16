package ru.practicum.ewm.compilation.controller;

import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.service.CompilationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> get(@RequestParam(defaultValue = "false") boolean pinned,
                                    @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                    @RequestParam(defaultValue = "10") @Positive int size) {
        final PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
        return compilationService.getCompilation(pinned, pageRequest);
    }

    @GetMapping("/{compId}")
    public CompilationDto getById(@PathVariable @PositiveOrZero int compId) {
        return compilationService.getCompilationById(compId);
    }
}