package com.example.main.category.service;

import com.example.main.category.dto.CategoryDto;
import com.example.main.category.dto.NewCategoryDto;

public interface AdminCategoryService {
    CategoryDto post(NewCategoryDto newCategoryDto);

    void delete(int catId);

    CategoryDto patch(CategoryDto categoryDto, int catId);
}