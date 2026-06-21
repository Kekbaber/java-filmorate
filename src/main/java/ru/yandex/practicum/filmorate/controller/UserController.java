package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.request.CreateUserRequest;
import ru.yandex.practicum.filmorate.dto.request.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.response.UserResponse;
import ru.yandex.practicum.filmorate.service.FriendshipService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FriendshipService friendshipService;

    @GetMapping
    public List<UserResponse> findAll() {
        log.debug("GET /users");
        List<UserResponse> users = userService.findAll();
        log.debug("GET /users -> returned {} users", users.size());
        return users;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody CreateUserRequest request) {
        log.info("POST /users: {}", request.getLogin());
        UserResponse created = userService.create(request);
        log.info("Created user with id={}", created.getId());
        return created;
    }

    @PutMapping
    public UserResponse update(@Valid @RequestBody UpdateUserRequest request) {
        log.info("PUT /users: {}", request.getId());
        UserResponse updated = userService.update(request);
        log.info("Updated user with id={}", request.getId());
        return updated;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable @Positive long id) {
        log.info("DELETE /users/{}", id);
        userService.delete(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void sendFriendRequest(
            @PathVariable(name = "id") @Positive long userId,
            @PathVariable @Positive long friendId) {
        log.info("PUT /users/{}/friends/{}", userId, friendId);
        friendshipService.addFriendRequest(userId, friendId);
    }

    @PutMapping("/{id}/friends/{friendId}/confirm")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void confirmFriendRequest(
            @PathVariable(name = "id") @Positive long userId,
            @PathVariable @Positive long friendId
    ) {
        log.info("PUT /users/{}/friends/{}/confirm", userId, friendId);
        friendshipService.confirmFriendRequest(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<UserResponse> getFriends(@PathVariable(name = "id") @Positive long userId) {
        log.debug("GET /users/{}/friends", userId);
        List<UserResponse> userFriends = friendshipService.findConfirmedFriends(userId);
        log.debug("Returned {} friends", userFriends.size());
        return userFriends;
    }

    @GetMapping("/{id}/friends/outgoing")
    public List<UserResponse> getOutgoingRequests(@PathVariable(name = "id") @Positive long userId) {
        log.info("GET /users/{}/friends/outgoing", userId);
        return friendshipService.findOutgoingRequests(userId);
    }

    @GetMapping("/{id}/friends/incoming")
    public List<UserResponse> getIncomingRequests(@PathVariable(name = "id") @Positive long userId) {
        log.info("GET /users/{}/friends/incoming", userId);
        return friendshipService.findIncomingRequests(userId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFriend(
            @PathVariable(name = "id") @Positive long userId,
            @PathVariable @Positive long friendId
    ) {
        log.info("DELETE /users/{}/friends/{}", userId, friendId);
        friendshipService.deleteFriend(userId, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<UserResponse> getCommonFriends(
            @PathVariable @Positive long id,
            @PathVariable @Positive long otherId
    ) {
        log.debug("GET /users/{}/friends/common/{}", id, otherId);
        List<UserResponse> commonFriends = friendshipService.findCommonFriends(id, otherId);
        log.debug("Returned {} common friends", commonFriends.size());
        return commonFriends;
    }
}