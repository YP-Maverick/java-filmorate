package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IdNotAllowedException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Cоздание пользователя
    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user) {
        log.info("POST запрос /users");

        if (user.getId() != null) {
            log.warn("UserId cannot be null");
            throw new IdNotAllowedException("UserId cannot be null");
        }
        return userService.createUser(user);
    }

    // Обновление пользователя
    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        log.info("PUT запрос /users");
        return userService.updateUser(user);
    }

    // Получение списка всех пользователей
    @GetMapping("/users")
    public List<User> getAllUsers() {
        log.info("GET запрос /users");
        return userService.getAllUsers();
    }

    //-------------------------
    // Операции с друзьями

    // Добавление в друзья
    @PutMapping("/users/{id}/friends/{friendId}")
    public User addFriend(
            @PathVariable("id") Long id,
            @PathVariable("friendId") Long friendId
    ) {
        log.info("PUT запрос /users/{id}/friends/{friendId}");
        return userService.addFriend(id, friendId);
    }

    // Удаление друга
    @DeleteMapping("/users/{id}/friends/{friendId}")
    public User deleteFriend(
            @PathVariable("id") Long id,
            @PathVariable("friendId") Long friendId
    ) {
        log.info("DELETE запрос /users/{id}/friends/{friendId}");
        return userService.deleteFriend(id, friendId);
    }

    // Получение всех друзей пользователя
    @GetMapping("/users/{id}/friends")
    public List<User> getFriends(
            @PathVariable("id") Long id
    ) {
        log.info("GET запрос /users/{id}/friends");
        return userService.getFriends(id);
    }

    // Получение списка общих друзей
    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonsFriends(
            @PathVariable("id") Long userId,
            @PathVariable("otherId") Long otherId
    ) {
        log.info("GET запрос /users/{id}/friends/common/{otherId}");
        return userService.getCommonsFriends(userId, otherId);
    }
}

