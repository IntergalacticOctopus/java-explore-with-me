package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface CategoryService {
    CategoryDto post(NewCategoryDto newCategoryDto);

    void delete(int catId);

    CategoryDto patch(CategoryDto categoryDto, int catId);

    List<CategoryDto> getCategories(PageRequest pageRequest);

    CategoryDto getById(int catId);
}
