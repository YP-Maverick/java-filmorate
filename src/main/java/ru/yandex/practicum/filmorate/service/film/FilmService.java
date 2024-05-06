package ru.yandex.practicum.filmorate.service.film;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;

import java.util.*;

@AllArgsConstructor
@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final RatingMpaStorage ratingMpaStorage;
    private final EventStorage eventStorage;
    private final DirectorStorage directorStorage;

    private void checkFilmAndUserId(Long filmId, Long userId) {
        if (!filmStorage.contains(filmId)) {
            log.error("Неверно указан id фильма: {}.", filmId);
            throw new NotFoundException(String.format("Фильма с id %d не существует.", filmId));
        } else if (!userStorage.contains(userId)) {
            log.error("Неверно указан id пользователя: {}.", userId);
            throw new NotFoundException(String.format("Пользователя с id %d не существует.", userId));
        }
    }

    public void addLike(Long filmId, Long userId) {
        checkFilmAndUserId(filmId, userId);
        filmStorage.addLike(filmId, userId);
        eventStorage.add("LIKE", "ADD", userId, filmId);
    }

    public void deleteLike(Long filmId, Long userId) {
        checkFilmAndUserId(filmId, userId);
        filmStorage.deleteLike(filmId, userId);
        eventStorage.add("LIKE", "REMOVE", userId, filmId);
    }

    public List<Film> getTopFilms(Integer count, Integer genreId, String year) {
        List<Film> filmsWithoutGenresAndDir = filmStorage.getTopFilms(count, genreId, year);
        List<Film> correctFilms = new ArrayList<>();
        for (Film film : filmsWithoutGenresAndDir) {
            Set<Genre> genres = genreStorage.getFilmGenres(film.getId());
            Set<Director> directors = directorStorage.getFilmDirectors(film.getId());
            Film correctFilm = film.withGenres(genres).withDirectors(directors);
            correctFilms.add(correctFilm);
        }
        return correctFilms;
    }

    public Film createFilm(Film film) {
        // Проверка существования жанров, рейтинга MPA и режиссёров
        genreStorage.checkGenres(film.getGenres());
        ratingMpaStorage.checkRatingId(film.getMpa().getId());
        directorStorage.checkDirectors(film.getDirectors());

        Film newFilm = filmStorage.create(film);
        genreStorage.addFilmGenres(newFilm.getId(), film.getGenres());
        directorStorage.addFilmDirectors(newFilm.getId(), film.getDirectors());
        return newFilm;
    }

    public Film deleteFilm(Long id) {
        Set<Genre> genres = genreStorage.getFilmGenres(id);
        Set<Director> directors = directorStorage.getFilmDirectors(id);

        return filmStorage.deleteFilm(id).withGenres(genres).withDirectors(directors);
    }

    public Film updateFilm(Film film) {
        // Проверка существования жанров, рейтинга MPA и режиссёров
        genreStorage.checkGenres(film.getGenres());
        ratingMpaStorage.checkRatingId(film.getMpa().getId());
        directorStorage.checkDirectors(film.getDirectors());

        Film updatedFilm = filmStorage.updateFilm(film);
        genreStorage.updateFilmGenres(film.getId(), film.getGenres());
        directorStorage.updateFilmDirectors(film.getId(), film.getDirectors());
        return updatedFilm;
    }

    public List<Film> findAllFilms() {
        List<Film> filmsWithoutGenresAndDir = filmStorage.findAllFilms();
        List<Film> correctFilms = new ArrayList<>();
        for (Film film : filmsWithoutGenresAndDir) {
            Set<Genre> genres = genreStorage.getFilmGenres(film.getId());
            Set<Director> directors = directorStorage.getFilmDirectors(film.getId());
            Film correctFilm = film.withGenres(genres).withDirectors(directors);
            correctFilms.add(correctFilm);
        }
        return correctFilms;
    }

    public Film getFilmById(Long id) {
        Film film = filmStorage.getFilmById(id);
        Set<Genre> genres = genreStorage.getFilmGenres(film.getId());
        Set<Director> directors = directorStorage.getFilmDirectors(film.getId());

        return film.withGenres(genres).withDirectors(directors);
    }

    public List<Film> getFilmsByDirector(Long directorId, String sortBy) {
        // Проверка directorId
        directorStorage.getById(directorId);

        List<Film> filmsWithoutGenresAndDir = filmStorage.getFilmsByDirector(directorId, sortBy);
        List<Film> correctFilms = new ArrayList<>();
        for (Film film : filmsWithoutGenresAndDir) {
            Set<Genre> genres = genreStorage.getFilmGenres(film.getId());
            Set<Director> directors = directorStorage.getFilmDirectors(film.getId());
            Film correctFilm = film.withGenres(genres).withDirectors(directors);
            correctFilms.add(correctFilm);
        }
        return correctFilms;
    }

    public List<Film> getRecommendations(Long userId) {
        List<Film> filmsWithoutGenresAndDir = filmStorage.getRecommendations(userId);
        List<Film> correctFilms = new ArrayList<>();
        for (Film film : filmsWithoutGenresAndDir) {
            Set<Genre> genres = genreStorage.getFilmGenres(film.getId());
            Set<Director> directors = directorStorage.getFilmDirectors(film.getId());
            Film correctFilm = film.withGenres(genres).withDirectors(directors);
            correctFilms.add(correctFilm);
        }
        return correctFilms;
    }

    public List<Film> getFilmsBySearch(String query, String by) {
        if ("director,title".contains(by) || "title,director".contains(by)) {
            List<Film> filmsWithoutGenresAndDir = filmStorage.getFilmsBySearch(query, by);
            List<Film> correctFilms = new ArrayList<>();
            for (Film film : filmsWithoutGenresAndDir) {
                Set<Genre> genres = genreStorage.getFilmGenres(film.getId());
                Set<Director> directors = directorStorage.getFilmDirectors(film.getId());
                Film correctFilm = film.withGenres(genres).withDirectors(directors);
                correctFilms.add(correctFilm);
            }
            return correctFilms;
        } else {
            throw new ValidationException("Значения параметра 'by' некорректны");
        }
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        return filmStorage.getCommonFilms(userId, friendId);
    }
}
