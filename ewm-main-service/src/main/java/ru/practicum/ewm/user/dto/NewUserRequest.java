package ru.practicum.ewm.user.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class NewUserRequest {
    @NotBlank
    @Email
    @Length(min = 6, max = 254, message = "Invalid lenght")
    private String email;

    @NotBlank
    @Length(min = 2, max = 250, message = "Invalid lenght")
    private String name;
}