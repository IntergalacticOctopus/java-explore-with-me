package com.example.main.category.controller;

import com.example.main.category.dto.CategoryDto;
import com.example.main.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
@Validated
public class PublicCategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> get(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                 @RequestParam(defaultValue = "10") @Positive int size) {
        final PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
        return categoryService.getCategories(pageRequest);
    }

    @GetMapping("/{catId}")
    public CategoryDto getById(@PathVariable @PositiveOrZero int catId) {
        return categoryService.getById(catId);
    }
}