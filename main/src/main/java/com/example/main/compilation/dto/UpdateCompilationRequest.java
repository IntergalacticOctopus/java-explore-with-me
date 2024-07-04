package com.example.main.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class UpdateCompilationRequest {
    private List<Integer> events;

    private Boolean pinned;

    @Length(min = 1, max = 50, message = "Invalid lenght")
    private String title;
}