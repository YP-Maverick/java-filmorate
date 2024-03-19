package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    void add(Film film);

    Film getFilmById(Long filmId);

    List<Film> getAllFilms();

}
