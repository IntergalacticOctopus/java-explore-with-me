package com.example.main.category.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class CategoryDto {
    private int id;
    @NotBlank
    @Length(min = 1, max = 50, message = "Invalid lenght")
    private String name;
}