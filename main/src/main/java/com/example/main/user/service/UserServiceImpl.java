package com.example.main.user.service;

import com.example.main.exception.model.DataConflictException;
import com.example.main.exception.model.EntityNotFoundException;
import com.example.main.user.dto.NewUserRequest;
import com.example.main.user.dto.UserDto;
import com.example.main.user.mapper.UserMapper;
import com.example.main.user.model.User;
import com.example.main.user.repository.UserRepository;
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
            throw new EntityNotFoundException("Data not found");
        }
        userRepository.deleteById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers(List<Integer> ids, int from, int size) {
        if (ids.isEmpty()) {
            final PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
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