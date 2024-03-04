package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;


@Slf4j
@RestController
public class UserController {

    private static final Set<User> users = new HashSet<>(); // Пока нет БД храним в контроллере

    // Cоздание пользователя;
    @PostMapping("/user")
    public User createUser(@Valid @RequestBody User user) {
        if (users.contains(user)) {
            log.warn("User уже существует");
            throw new AlreadyExistException("User уже существует");
        }

        log.info("Обработан POST запрос /user");
        users.add(user);
        return user;
    }

    // Обновление пользователя;
    @PutMapping("/user")
    public User updateUser(@Valid @RequestBody User user) {

        users.removeIf(existUser -> existUser.getEmail().equals(user.getEmail()));
        users.add(user);
        log.info("Обработан PUT запрос /user");
        return user;
    }

    // Получение списка всех пользователей.
    @GetMapping("/users")
    public Set<User> getAllUsers() {

        log.info("Обработан GET запрос /users");
        return users;
    }
}
