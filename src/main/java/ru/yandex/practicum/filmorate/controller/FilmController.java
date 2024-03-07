package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IdNotAllowedException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
public class FilmController {

    private static final List<Film> films = new ArrayList<>(); // Пока нет БД храним в контроллере
    private static int idCounter = 1;

    // Cоздание пользователя;
    @PostMapping("/films")
    public Film createFilm(@Valid @RequestBody Film film)  {
        if (film.getId() != null) {
            throw new IdNotAllowedException("ID != NULL");
        }
        film.setId(generateId());
        films.add(film);
        log.info("Обработан POST запрос /film");
        return film;
    }


    // Обновление пользователя;
    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (film.getId() != null) {
            boolean isExist = films.stream().anyMatch(existingFilm -> Objects.equals(existingFilm.getId(), film.getId()));
            if (!isExist) {
                throw new NotFoundException("Film with this ID not found");
            }
            films.removeIf(existingFilm -> Objects.equals(existingFilm.getId(), film.getId()));
            films.add(film);
        } else {
            film.setId(generateId());
            films.add(film);
        }
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
