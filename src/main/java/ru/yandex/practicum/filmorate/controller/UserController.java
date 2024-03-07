package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@RestController
public class UserController {

    private static final List<User> users = new ArrayList<>(); // Пока нет БД храним в контроллере

    // Cоздание пользователя;
    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user) {
        if (users.contains(user)) {
            log.warn("User уже существует");
            throw new AlreadyExistException("User уже существует");
        }

        log.info("Обработан POST запрос /user");
        replaceNameWithLogin(user);
        users.add(user);
        return user;
    }

    // Обновление пользователя;
    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {

        users.removeIf(existUser -> existUser.getId() == user.getId());
        users.add(user);
        log.info("Обработан PUT запрос /user");
        replaceNameWithLogin(user);
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
}
