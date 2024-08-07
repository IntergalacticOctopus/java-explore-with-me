package ru.practicum.ewm.user.service;

import ru.practicum.ewm.exception.errors.DataConflictException;
import ru.practicum.ewm.exception.errors.NotFoundException;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto postUser(NewUserRequest newUserRequest) {
        if (userRepository.existsByEmail(newUserRequest.getEmail())) {
            throw new DataConflictException("Email already exists");
        }
        User user = userMapper.createUser(newUserRequest);
        return userMapper.createUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(int userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Data not found");
        }
        userRepository.deleteById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers(List<Integer> ids, PageRequest pageRequest) {
        if (ids.isEmpty()) {

            return userRepository.findAll(pageRequest)
                    .map(userMapper::createUserDto)
                    .getContent();
        }
        return userRepository.getAllUsersById(ids)
                .stream()
                .map(userMapper::createUserDto)
                .collect(Collectors.toList());
    }
}