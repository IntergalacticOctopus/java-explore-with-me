package com.example.main.events.dto;

import com.example.main.category.dto.CategoryDto;
import com.example.main.user.dto.UserShortDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class EventShortDto {
    private int id;

    private String annotation;

    private CategoryDto category;

    private int confirmedRequests;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private UserShortDto initiator;

    private boolean paid;

    private String title;

    private int views;
}