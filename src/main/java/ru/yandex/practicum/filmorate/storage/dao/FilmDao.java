package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmDao {

    GenreDao getGenreDao();

    MpaDao getMpaDao();

    Film createFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(Long filmId);

    List<Film> getAllFilms();

    Film getFilmById(Long id);

    List<Film> getPopularFilms(int count);

    Boolean checkFilmId(Long id);

    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);
}