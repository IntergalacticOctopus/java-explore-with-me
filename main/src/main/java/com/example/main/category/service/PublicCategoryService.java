package com.example.main.category.service;


import com.example.main.category.dto.CategoryDto;

import java.util.List;

public interface PublicCategoryService {
    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getById(int catId);
}