package com.example.main.user.mapper;

import com.example.main.user.dto.NewUserRequest;
import com.example.main.user.dto.UserDto;
import com.example.main.user.dto.UserShortDto;
import com.example.main.user.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDto createUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public User createUser(NewUserRequest newUserRequest) {
        return User.builder()
                .email(newUserRequest.getEmail())
                .name(newUserRequest.getName())
                .build();
    }

    public UserShortDto createUserShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}