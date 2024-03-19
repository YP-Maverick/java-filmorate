package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private static long idCounter = 1;
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    // Cоздание пользователя;
    public Film createFilm(Film film)  {
        film.setId(generateId());
        filmStorage.add(film);
        return film;
    }

    // Обновление пользователя;
    public Film updateFilm(Film film) {
        if (film.getId() != null) {
            boolean isExist = filmStorage.getAllFilms().stream().anyMatch(existingFilm -> Objects.equals(existingFilm.getId(), film.getId()));
            if (!isExist) {
                throw new NotFoundException("Film with this ID not found");
            }
            filmStorage.getAllFilms().removeIf(existingFilm -> Objects.equals(existingFilm.getId(), film.getId()));
            filmStorage.add(film);
        } else {
            film.setId(generateId());
            filmStorage.add(film);
        }
        return film;
    }

    // Получение списка всех пользователей.
    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    // User cтавит лайк
    public Film addLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId);

        if (film == null) {
            throw new NotFoundException("Film with this ID not found");
        }

        film.getLikedUsersId().add(userId);
        return film;
    }

    // User удаляет лайк
    public Film deleteLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId);

        if (film == null) {
            throw new NotFoundException("Film with this ID not found");
        }

        film.getLikedUsersId().remove(userId);
        return film;
    }

    // Возвращает список из первых count фильмов по количеству лайков.
    public List<Film> getPopularFilms(int count) {
        if (count == 0) count = 10;
        return filmStorage.getAllFilms()
                .stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikedUsersId().size())
                        .reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    private long generateId() {
        return idCounter++;
    }
}
