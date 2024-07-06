package com.example.main.category.service;

import com.example.main.category.dto.CategoryDto;
import com.example.main.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto post(NewCategoryDto newCategoryDto);

    void delete(int catId);

    CategoryDto patch(CategoryDto categoryDto, int catId);

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getById(int catId);
}
