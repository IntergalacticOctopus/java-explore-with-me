package ru.practicum.ewm.events.dto;

import ru.practicum.ewm.events.model.Location;
import ru.practicum.ewm.events.model.StateAction;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class UpdateEventUserRequest {
    @Length(min = 20, max = 2000, message = "Invalid lenght")
    private String annotation;

    private Integer category;

    @Length(min = 20, max = 7000, message = "Invalid lenght")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Location location;

    private Boolean paid;

    @PositiveOrZero
    private Integer participantLimit;

    private Boolean requestModeration;

    private StateAction stateAction;

    @Length(min = 3, max = 120, message = "Invalid lenght")
    private String title;
}