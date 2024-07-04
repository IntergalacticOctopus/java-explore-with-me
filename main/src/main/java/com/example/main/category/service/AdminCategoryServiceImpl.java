package com.example.main.category.service;

import com.example.main.category.dto.CategoryDto;
import com.example.main.category.dto.NewCategoryDto;
import com.example.main.category.mapper.CategoryMapper;
import com.example.main.category.model.Category;
import com.example.main.category.repository.CategoryRepository;
import com.example.main.exception.model.DataConflictException;
import com.example.main.exception.model.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminCategoryServiceImpl implements AdminCategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto post(NewCategoryDto newCategoryDto) {
        if (categoryRepository.existsByName(newCategoryDto.getName())) {
            throw new DataConflictException("Category name already exist");
        }
        final Category category = categoryMapper.toCategory(newCategoryDto);
        return categoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void delete(int categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new EntityNotFoundException("Data not found");
        }
        categoryRepository.deleteById(categoryId);
    }

    @Override
    @Transactional
    public CategoryDto patch(CategoryDto categoryDto, int categoryId) {
        final Category category = categoryRepository.findById(categoryId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Data not found")
                );
        if (categoryDto.getName().equals(category.getName())) {
            return categoryMapper.toCategoryDto(category);
        }
        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new DataConflictException("Category name already exist");
        }
        category.setName(categoryDto.getName());
        return categoryMapper.toCategoryDto(categoryRepository.save(category));
    }
}