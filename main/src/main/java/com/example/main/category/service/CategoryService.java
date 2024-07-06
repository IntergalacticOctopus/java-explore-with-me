package com.example.main.category.service;

import com.example.main.category.dto.CategoryDto;
import com.example.main.category.dto.NewCategoryDto;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface CategoryService {
    CategoryDto post(NewCategoryDto newCategoryDto);

    void delete(int catId);

    CategoryDto patch(CategoryDto categoryDto, int catId);

    List<CategoryDto> getCategories(PageRequest pageRequest);

    CategoryDto getById(int catId);
}
