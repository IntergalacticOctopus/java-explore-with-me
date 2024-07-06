package com.example.main.user.service;

import com.example.main.user.dto.NewUserRequest;
import com.example.main.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto postUser(NewUserRequest newUserRequest);

    void deleteUser(int userId);

    List<UserDto> getAllUsers(List<Integer> ids, int from, int size);
}