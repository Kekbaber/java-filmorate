package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.request.CreateUserRequest;
import ru.yandex.practicum.filmorate.dto.request.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.response.UserResponse;

import java.util.Collection;
import java.util.List;

public interface UserService {
    List<UserResponse> findAll();

    UserResponse findById(long id);

    UserResponse create(CreateUserRequest request);

    UserResponse update(UpdateUserRequest request);

    void delete(long id);

    List<UserResponse> findAllByIds(Collection<Long> ids);
}
