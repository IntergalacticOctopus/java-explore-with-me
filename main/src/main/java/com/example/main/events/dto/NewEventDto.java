package com.example.main.events.dto;

import com.example.main.events.model.Location;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class NewEventDto {
    @NotBlank
    @NotNull
    @Length(min = 20, max = 2000, message = "Invalid lenght")
    private String annotation;

    @Positive
    private int category;

    @NotBlank
    @NotNull
    @Length(min = 20, max = 7000, message = "Invalid lenght")
    private String description;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull
    private Location location;
    private Boolean paid = false;

    @PositiveOrZero
    private int participantLimit = 0;
    private Boolean requestModeration = true;

    @NotBlank
    @NotNull
    @Length(min = 3, max = 120, message = "Invalid lenght")
    private String title;
}