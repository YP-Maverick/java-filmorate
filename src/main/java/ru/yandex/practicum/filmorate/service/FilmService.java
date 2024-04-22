package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.FilmDao;
import ru.yandex.practicum.filmorate.storage.dao.GenreDao;
import ru.yandex.practicum.filmorate.storage.dao.MpaDao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmDao filmDao;
    private final GenreDao genreDao;
    private final MpaDao mpaDao;


    private void validateFilmId(Long filmId) {
        if (!filmDao.checkFilmId(filmId)) {
            throw new NotFoundException("Film with this ID not found");
        }
    }

    private void validateMpaAndGenres(Film film) {

        if (!mpaDao.checkMpaId(film.getMpa().getId())) {
            throw new EntityNotFoundException("Invalid MPA ID");
        }
        if (film.getGenres() != null && !genreDao.checkAllGenresExist(
                film.getGenres().stream().map(Genre::getId).collect(Collectors.toList()))) {
            throw new EntityNotFoundException("Invalid Genre ID");
        }
    }

    // Создание фильма
    public Film createFilm(Film film) {
        validateMpaAndGenres(film);

        Film res = filmDao.createFilm(film);

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            genreDao.saveGenresByFilmId(film.getId(), new ArrayList<>(film.getGenres()));
        }
        return res;
    }

    // Обновление фильма
    public Film updateFilm(Film film) {
        validateFilmId(film.getId());
        validateMpaAndGenres(film);
        return filmDao.updateFilm(film);
    }


    // Удаление фильма
    public void deleteFilm(Long filmId) {
        validateFilmId(filmId);
        filmDao.deleteFilm(filmId);
    }

    // Получение фильма
    public Film getFilmById(Long filmId) {

        validateFilmId(filmId);

        Film res = filmDao.getFilmById(filmId);
        res.setMpa(mpaDao.getMpaByFilmId(res.getId()));
        res.setGenres(new HashSet<>(genreDao.getGenresByFilmId(res.getId())));
        return res;

    }

    // Получение всех фильмов
    public List<Film> getAllFilms() {
        List<Film> films = filmDao.getAllFilms();
        for (Film film : films) {
            film.setMpa(mpaDao.getMpaByFilmId(film.getId()));
            film.setGenres(new HashSet<>(genreDao.getGenresByFilmId(film.getId())));
        }
        return films;
    }

    // Добавление лайка
    public void addLike(Long filmId, Long userId) {
        if (!filmDao.checkFilmId(filmId)) {
            throw new NotFoundException("Film with this ID not found");
        }
        filmDao.addLike(filmId, userId);
    }

    // Удаление лайка
    public void deleteLike(Long filmId, Long userId) {

        validateFilmId(filmId);
        filmDao.deleteLike(filmId, userId);
    }

    // Получение популярный фильмов
    public List<Film> getPopularFilms(int count) {

        if (count == 0) count = 10;

        List<Film> popularFilms = filmDao.getPopularFilms(count);
        for (Film film : popularFilms) {
            film.setMpa(mpaDao.getMpaByFilmId(film.getId()));
            film.setGenres(new HashSet<>(genreDao.getGenresByFilmId(film.getId())));
        }
        return popularFilms;
    }

    // Получение списка возрастного рейтинга
    public Mpa getMpa(Long mpaId) {

        if (mpaDao.checkMpaId(mpaId)) {
            return mpaDao.getMpaById(mpaId);
        } else {
            throw new NotFoundException("Genre with this ID not found");
        }
    }

    // Получение всех возрастных рейтингов
    public List<Mpa> getAllMpa() {
        return mpaDao.getAllMpa();
    }


    // Получениче жанра по id
    public Genre getGenre(Long genreId) {
        if (genreDao.checkGenreId(genreId)) {
            return genreDao.getGenreById(genreId);
        } else {
            throw new NotFoundException("Genre with this ID not found");
        }
    }

    // Получение всех возрастных рейтингов
    public List<Genre> getAllGenres() {

        return genreDao.getAllGenres();
    }
}