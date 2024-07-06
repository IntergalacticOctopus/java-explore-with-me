package com.example.main.category.service;

import com.example.main.category.dto.CategoryDto;
import com.example.main.category.dto.NewCategoryDto;
import com.example.main.category.mapper.CategoryMapper;
import com.example.main.category.model.Category;
import com.example.main.category.repository.CategoryRepository;
import com.example.main.exception.errors.DataConflictException;
import com.example.main.exception.errors.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto post(NewCategoryDto newCategoryDto) {
        final Category category = categoryMapper.toCategory(newCategoryDto);
        return categoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void delete(int categoryId) {
        categoryRepository.deleteById(categoryId);
    }

    @Override
    @Transactional
    public CategoryDto patch(CategoryDto categoryDto, int categoryId) {
        final Category category = categoryRepository.findById(categoryId)
                .orElseThrow(
                        () -> new NotFoundException("Data not found")
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

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getCategories(PageRequest pageRequest) {
        return categoryRepository.findAll(pageRequest).getContent().stream()
                .map(categoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getById(int categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(
                        () -> new NotFoundException("Data not found")
                );
        return categoryMapper.toCategoryDto(category);
    }
}
