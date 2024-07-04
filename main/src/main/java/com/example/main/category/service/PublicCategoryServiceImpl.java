package com.example.main.category.service;

import com.example.main.category.dto.CategoryDto;
import com.example.main.category.mapper.CategoryMapper;
import com.example.main.category.model.Category;
import com.example.main.category.repository.CategoryRepository;
import com.example.main.exception.model.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PublicCategoryServiceImpl implements PublicCategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getCategories(int from, int size) {
        final PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
        return categoryRepository.findAll(pageRequest).getContent().stream()
                .map(categoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getById(int categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Data not found")
                );
        return categoryMapper.toCategoryDto(category);
    }
}