package ru.practicum.ewm.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class NewCompilationDto {
    private Set<Integer> events;

    private Boolean pinned;

    @NotBlank
    @Length(min = 1, max = 50, message = "Invalid lenght")
    private String title;

}