package ru.practicum.ewm.user.service;

import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface UserService {
    UserDto postUser(NewUserRequest newUserRequest);

    void deleteUser(int userId);

    List<UserDto> getAllUsers(List<Integer> ids, PageRequest pageRequest);
}