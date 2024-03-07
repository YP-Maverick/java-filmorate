package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class FilmController {

    private static final List<Film> films = new ArrayList<>(); // Пока нет БД храним в контроллере
    private static int idCounter = 0;

    // Cоздание пользователя;
    @PostMapping("/films")
    public Film createFilm(@Valid @RequestBody Film film) {
        if (films.stream().anyMatch(existingFilm -> existingFilm.getId() == film.getId())) {
            log.warn("Film уже существует");
            throw new AlreadyExistException("Film уже существует");
        }
        film.setId(generateId());
        films.add(film);
        log.info("Обработан POST запрос /film");
        return film;
    }

    // Обновление пользователя;
    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        films.removeIf(existingFilm -> existingFilm.getId() == film.getId());
        films.add(film);
        log.info("Обработан PUT запрос /film");
        return film;
    }

    // Получение списка всех пользователей.
    @GetMapping("/films")
    public List<Film> getAllFilms() {
        return films;
    }

    private Integer generateId() {
        return idCounter++;
    }
}
