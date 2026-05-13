package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FriendshipService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validation.group.OnUpdate;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FriendshipService friendshipService;

    @GetMapping
    public Collection<User> findAll() {
        log.debug("GET /users");
        Collection<User> users = userService.findAll();
        log.debug("GET /user -> returned {} users", users.size());
        return users;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody User user) {
        log.info("POST /users: {}", user.getLogin());
        User created = userService.create(user);
        log.info("Created user with id={}", user.getId());
        return created;
    }

    @PutMapping
    public User update(@Validated(OnUpdate.class) @RequestBody User user) {
        log.info("PUT /users: {}", user.getId());
        User updated = userService.update(user);
        log.info("Updated user with id={}", user.getId());
        return updated;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        log.info("DELETE /users/{}", id);
        userService.delete(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addFriend(
            @PathVariable(name = "id") @Positive long userId,
            @PathVariable @Positive long friendId) {
        log.info("PUT /users/{}/friends/{}", userId, friendId);
        friendshipService.add(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable(name = "id") @Positive long userId) {
        log.debug("GET /users/{}/friends", userId);
        Collection<User> userFriends = friendshipService.get(userId);
        log.debug("Returned {} friends", userFriends.size());
        return userFriends;
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFriend(
            @PathVariable(name = "id") long userId,
            @PathVariable long friendId
    ) {
        log.info("DELETE /users/{}/friends/{}", userId, friendId);
        friendshipService.remove(userId, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(
            @PathVariable long id,
            @PathVariable long otherId
    ) {
        log.debug("GET /users/{}/friends/common/{}", id, otherId);
        Collection<User> commonFriends = friendshipService.getCommonFriends(id, otherId);
        log.debug("Returned {} common friends", commonFriends.size());
        return commonFriends;
    }
}



