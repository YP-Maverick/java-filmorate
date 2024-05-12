package ru.yandex.practicum.filmorate.service.film;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
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
        eventStorage.add(EventType.LIKE.toString(), EventOperation.ADD.toString(), userId, filmId);
    }

    public void addMark(Long filmId, Long userId, Integer mark) {
        if (mark < 1 || mark > 10) {
            throw new IllegalArgumentException("Оценка должна быть в диапазоне от 1 до 10.");
        }
        checkFilmAndUserId(filmId, userId);
        filmStorage.addMark(filmId, userId, mark);
        addLike(filmId, userId);
        eventStorage.add(EventType.MARK.toString(), EventOperation.ADD.toString(), userId, filmId);
    }

    public void deleteLike(Long filmId, Long userId) {
        checkFilmAndUserId(filmId, userId);
        filmStorage.deleteLike(filmId, userId);
        eventStorage.add(EventType.LIKE.toString(), EventOperation.REMOVE.toString(), userId, filmId);
    }

    public void deleteMark(Long filmId, Long userId) {
        checkFilmAndUserId(filmId, userId);
        filmStorage.deleteMark(filmId, userId);
        deleteLike(filmId, userId);
        eventStorage.add(EventType.MARK.toString(), EventOperation.REMOVE.toString(), userId, filmId);
    }

    public List<Film> getTopFilms(Integer count, Integer genreId, String year) {
        return filmStorage.getTopFilms(count, genreId, year);
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
        return filmStorage.findAllFilms();
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

        return filmStorage.getFilmsByDirector(directorId, sortBy);
    }

    public List<Film> getRecommendations(Long userId) {
        return filmStorage.getRecommendations(userId);
    }

    public List<Film> getFilmsBySearch(String query, String by) {
        if ("director,title".contains(by) || "title,director".contains(by)) {
            return filmStorage.getFilmsBySearch(query, by);
        } else {
            throw new ValidationException("Значения параметра 'by' некорректны");
        }
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        return filmStorage.getCommonFilms(userId, friendId);
    }
}
