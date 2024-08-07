package ru.practicum.ewm.events.dto;


import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.events.model.EventState;
import ru.practicum.ewm.events.model.Location;
import ru.practicum.ewm.user.dto.UserShortDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class EventFullDto {
    private int id;

    @NotBlank
    @Size(max = 2000)
    private String annotation;

    @NotBlank
    @Size(max = 7000)
    private String description;

    @NotNull
    private CategoryDto category;

    private int confirmedRequests;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull
    private UserShortDto initiator;

    @NotNull
    private Location location;

    @NotNull
    private Boolean paid;

    private int participantLimit;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;

    private Boolean requestModeration;

    private EventState state;

    @NotBlank
    @Size(max = 255)
    private String title;

    private int views;
}