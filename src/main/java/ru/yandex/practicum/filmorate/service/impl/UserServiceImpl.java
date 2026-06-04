package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public List<UserResponse> findAll() {
        log.debug("Find all users");
        List<UserResponse> users = userStorage.findAll().stream()
                .map(UserMapper::toResponse)
                .toList();
        log.debug("Found {} users", users.size());
        return users;
    }

    @Override
    public UserResponse findById(long id) {
        log.debug("Find user by id={}", id);
        return userStorage.findById(id).map(UserMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + id + " не найден"));
    }

    @Override
    @Transactional
    public UserResponse create(CreateUserRequest request) {
        log.debug("Create user: login={}, email={}", request.getLogin(), request.getEmail());
        User user = UserMapper.toEntity(request);
        User created = userStorage.create(user);
        log.debug("Created user with id={}", created.getId());
        return UserMapper.toResponse(created);
    }

    @Override
    @Transactional
    public UserResponse update(UpdateUserRequest request) {
        log.debug("Update user id={}, login={}", request.getId(), request.getLogin());
        if (userStorage.findById(request.getId()).isEmpty()) {
            log.warn("Attempt to update non-existing user id={}", request.getId());
            throw new NotFoundException("Пользователь с id = " + request.getId() + " не найден");
        }
        User user = UserMapper.toEntity(request);
        User updated = userStorage.update(user);
        log.debug("Updated user id={}", updated.getId());
        return UserMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void delete(long id) {
        log.debug("Delete user id={}", id);
        findById(id);
        userStorage.delete(id);
        log.debug("Deleted user id={}", id);
    }

    @Override
    public List<UserResponse> findAllByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        List<User> users = userStorage.findAllByIds(ids);
        return users.stream()
                .map(UserMapper::toResponse)
                .toList();

    }
}
