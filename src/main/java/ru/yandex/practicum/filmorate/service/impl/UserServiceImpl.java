package ru.yandex.practicum.filmorate.service.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.request.CreateUserRequest;
import ru.yandex.practicum.filmorate.dto.request.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.response.UserResponse;
import ru.yandex.practicum.filmorate.exception.model.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private final UserMapper userMapper;

    @Override
    public List<UserResponse> findAll() {
        log.debug("Find all users");
        List<UserResponse> users = userStorage.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
        log.debug("Found {} users", users.size());
        return users;
    }

    @Override
    public UserResponse findById(long id) {
        log.debug("Find user by id={}", id);
        return userStorage.findById(id).map(userMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + id + " не найден"));
    }

    @Override
    public UserResponse create(@Valid CreateUserRequest request) {
        log.info("Create user: login={}, email={}", request.getLogin(), request.getEmail());
        User user = userMapper.toEntity(request);
        User created = userStorage.create(user);
        log.info("Created user with id={}", created.getId());
        return userMapper.toResponse(created);
    }

    @Override
    public UserResponse update(UpdateUserRequest request) {
        log.info("Update user id={}, login={}", request.getId(), request.getLogin());
        if (userStorage.findById(request.getId()).isEmpty()) {
            log.warn("Attempt to update non-existing user id={}", request.getId());
            throw new NotFoundException("Пользователь с id = " + request.getId() + " не найден");
        }
        User user = userMapper.toEntity(request);
        User updated = userStorage.update(user);
        log.info("Updated user id={}", updated.getId());
        return userMapper.toResponse(updated);
    }

    public void delete(long id) {
        log.info("Delete user id={}", id);
        if (userStorage.findById(id).isEmpty()) {
            log.warn("Attempt to delete non-existing user id={}", id);
        }
        userStorage.delete(id);
        log.info("Deleted user id={}", id);
    }

    @Override
    public List<UserResponse> findAllByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        List<User> users = userStorage.findAllByIds(ids);
        return users.stream()
                .map(userMapper::toResponse)
                .toList();

    }
}
