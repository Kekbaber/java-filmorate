package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.request.CreateUserRequest;
import ru.yandex.practicum.filmorate.dto.request.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.response.UserResponse;
import ru.yandex.practicum.filmorate.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static User toEntity(CreateUserRequest request) {
        if (request == null) return null;
        User user = new User();
        user.setEmail(request.getEmail());
        user.setLogin(request.getLogin());
        user.setName(checkName(request.getName(), request.getLogin()));
        user.setBirthday(request.getBirthday());
        return user;
    }

    public static User toEntity(UpdateUserRequest request) {
        if (request == null) return null;
        User user = new User();
        user.setId(request.getId());
        user.setEmail(request.getEmail());
        user.setLogin(request.getLogin());
        user.setName(checkName(request.getName(), request.getLogin()));
        user.setBirthday(request.getBirthday());
        return user;
    }

    public static UserResponse toResponse(User user) {
        if (user == null) return null;
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setLogin(user.getLogin());
        String name = user.getName();
        response.setName((name == null || name.isBlank()) ? user.getLogin() : name);
        response.setBirthday(user.getBirthday());
        return response;
    }

    public static String checkName(String name, String login) {
        return (name == null || name.isBlank()) ? login : name;
    }
}