package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class FilmController {

    private static final List<Film> films = new ArrayList<>(); // Пока нет БД храним в контроллере

    // Cоздание пользователя;
    @PostMapping("/films")
    public Film createFilm(@Valid @RequestBody Film film) {
        if (films.contains(film)) {
            log.warn("Film уже существует");
        }

        films.add(film);
        log.info("Обработан POST запрос /film");
        return film;
    }

    // Обновление пользователя;
    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        films.removeIf(existFilm -> existFilm.getId() == film.getId());
        films.add(film);
        log.info("Обработан PUT запрос /film");
        return film;
    }

    // Получение списка всех пользователей.
    @GetMapping("/films")
    public List<Film> getAlLFilms() {
        return films;
    }
}
