package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IdNotAllowedException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    // Cоздание пользователя;
    @PostMapping("/films")
    public Film createFilm(@Valid @RequestBody Film film)  {
        log.info("POST запрос /film");
        if (film.getId() != null) {
            throw new IdNotAllowedException("FilmId cannot be null");
        }
        return filmService.createFilm(film);
    }


    // Обновление пользователя;
    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("PUT запрос /film");
        return filmService.updateFilm(film);
    }

    // Получение списка всех пользователей.
    @GetMapping("/films")
    public List<Film> getAllFilms() {
        log.info("GET запрос /films");
        return filmService.getAllFilms();
    }


    //--------------------------------------------------
    // Функционал лайков

    // User cтавит лайк
    @PutMapping("/films/{id}/like/{userId}")
    public Film addLike(
            @PathVariable("id") Long filmId,
            @PathVariable("userId") Long userId
    ) {
        log.info("POST запрос /films/{id}/like/{userId}");
        return filmService.addLike(filmId, userId);
    }

    // User удаляет лайк
    @DeleteMapping("/films/{id}/like/{userId}")
    public Film deleteLike(
            @PathVariable("id") Long filmId,
            @PathVariable("userId") Long userId
    ) {
        log.info("POST запрос /films/{id}/like/{userId}");
        return filmService.deleteLike(filmId, userId);
    }

    // Возвращает список из первых count фильмов по количеству лайков.
    // Если значение параметра count не задано, вернет первые 10.
    @GetMapping("/films/popular")
    public List<Film> getPopularFilms(
            @RequestParam("count") int count
    ) {
        log.info("POST запрос /films/popular");
        return filmService.getPopularFilms(count);
    }

}
