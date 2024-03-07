package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IdNotAllowedException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
public class UserController {

    private static final List<User> users = new ArrayList<>(); // Пока нет БД храним в контроллере
    private static int idCounter = 1;

    // Cоздание пользователя;
    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user) {
        if (user.getId() != null) {
            log.warn("id not null");
            throw new IdNotAllowedException("id not null\"");
        }
        log.info("Обработан POST запрос /users");
        replaceNameWithLogin(user);
        user.setId(generateId());
        users.add(user);
        return user;
    }

    // Обновление пользователя;
    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        replaceNameWithLogin(user);
        if (user.getId() != null) {
            boolean isExist = users.stream().anyMatch(existingUser -> Objects.equals(existingUser.getId(), user.getId()));
            if (!isExist) {
                throw new NotFoundException("User with this ID not found");
            }
            users.removeIf(existingUser -> Objects.equals(existingUser.getId(), user.getId()));
            users.add(user);
        } else {
            user.setId(generateId());
            users.add(user);
        }
        log.info("Обработан PUT запрос /users");
        return user;
    }

    // Получение списка всех пользователей.
    @GetMapping("/users")
    public List<User> getAllUsers() {
        log.info("Обработан GET запрос /users");
        return users;
    }

    public void replaceNameWithLogin(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
    }

    private Integer generateId() {
        return idCounter++;
    }
}

