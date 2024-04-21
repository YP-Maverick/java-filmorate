package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
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
        return filmService.createFilm(film);
    }


    // Обновление пользователя;
    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("PUT запрос /film");
        return filmService.updateFilm(film);
    }

    // Удаление фильма

    @DeleteMapping("/films/{id}")
    public void deleteFilmById(@PathVariable Long id) {
        log.warn("DELETE запрос /films/{}", id);
        filmService.deleteFilm(id);
    }


    // Получение фильма по индефикатору
    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable Long id) {
        log.info("GET запрос /films/{}", id);
        return filmService.getFilmById(id);
    }

    // Получение списка всех пользователей.
    @GetMapping("/films")
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    //--------------------------------------------------
    // Функционал лайков

    // User cтавит лайк
    @PutMapping("/films/{id}/like/{userId}")
    public void addLike(
            @PathVariable("id") Long filmId,
            @PathVariable("userId") Long userId
    ) {
        log.info("POST запрос /films/{id}/like/{userId}");
        filmService.addLike(filmId, userId);
    }

    // User удаляет лайк
    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLike(
            @PathVariable("id") Long filmId,
            @PathVariable("userId") Long userId
    ) {
        log.info("POST запрос /films/{id}/like/{userId}");
        filmService.deleteLike(filmId, userId);
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

    @GetMapping("/mpa/{id}")
    public Mpa getMpa(
            @PathVariable("id") Long mpaId
    ) {
        log.info("GET запрос /mpa/{id}");
        return filmService.getMpa(mpaId);
    }

    @GetMapping("/mpa")
    public List<Mpa> getAllMpa() {
        log.info("GET запрос /films/popular");
        return filmService.getAllMpa();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenres(
            @PathVariable("id") Long genreId
    ) {
        log.info("GET запрос /genres/{id}");
        return filmService.getGenre(genreId);
    }

    @GetMapping("/genres")
    public List<Genre> getAllGenres() {
        log.info("GET запрос /genres");
        return filmService.getAllGenres();
    }

}
