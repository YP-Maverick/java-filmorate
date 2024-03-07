package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@RestController
public class FilmController {

    private static final Set<Film> films = new HashSet<>(); // Пока нет БД храним в контроллере

    // Cоздание пользователя;
    @PostMapping("/films")
    public Film createFilm(@Valid @RequestBody Film film) {
        if (films.contains(film)) {
            log.warn("Film уже существует");
        }

        log.info("Обработан POST запрос /film");
        films.add(film);
        return film;
    }

    // Обновление пользователя;
    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        films.removeIf(existFilm -> existFilm.getName().equals(film.getName())
                                 && existFilm.getReleaseDate().equals(film.getReleaseDate()));
        films.add(film);
        log.info("Обработан PUT запрос /film");
        return film;
    }

    // Получение списка всех пользователей.
    @GetMapping("/films")
    public Set<Film> getAlLFilms() {
        return films;
    }
}
