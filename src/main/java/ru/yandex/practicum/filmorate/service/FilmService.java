package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.FilmDao;
import ru.yandex.practicum.filmorate.storage.dao.GenreDao;
import ru.yandex.practicum.filmorate.storage.dao.MpaDao;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    private final FilmDao filmDao;

    public FilmService(FilmDao userDao) {
        this.filmDao = userDao;
    }

    // Cоздание пользователя;
    public Film createFilm(Film film) {

        MpaDao mpaDao = filmDao.getMpaDao();
        Long mpaId = film.getMpa().getId();

        GenreDao genreDao = filmDao.getGenreDao();
        Set<Genre> genres = film.getGenres();

        boolean isMpaValid = mpaDao.checkMpaId(mpaId) || mpaId == null;

        boolean areGenresValid = genres == null ||
                genreDao.checkAllGenresExist(genres.stream()
                .mapToLong(Genre::getId)
                .boxed()
                .collect(Collectors.toList()));

        if (isMpaValid && areGenresValid) {
            return filmDao.createFilm(film);
        } else {
            throw new EntityNotFoundException("MPA with this ID not created or invalid Genre ID");
        }
    }



    // Обновление пользователя;
    public Film updateFilm(Film film) {

        if (filmDao.checkFilmId(film.getId())) {
            return filmDao.updateFilm(film);
        } else {
            throw new NotFoundException("User with this ID not found");
        }
    }

    public void deleteFilm(Long filmId) {
        filmDao.deleteFilm(filmId);
    }

    // Получение фильма по id
    public Film getFilmById(Long filmId) {

        if (filmDao.checkFilmId(filmId)) {
            return filmDao.getFilmById(filmId);
        } else {
            throw new NotFoundException("Film with this ID not found");
        }

    }


    // Получение списка всех пользователей.
    public List<Film> getAllFilms() {
        return filmDao.getAllFilms();
    }

    // User cтавит лайк
    public void addLike(Long filmId, Long userId) {

        log.warn("FILM ID CHECK " + filmId);

        if (!filmDao.checkFilmId(filmId)) {
            throw new NotFoundException("Film with this ID not found");
        }
        filmDao.addLike(filmId, userId);
    }

    // User удаляет лайк
    public void deleteLike(Long filmId, Long userId) {

        if (!filmDao.checkFilmId(filmId)) {
            throw new NotFoundException("Film with this ID not found");
        }

       filmDao.deleteLike(filmId, userId);
    }

    // Возвращает список из первых count фильмов по количеству лайков.
    public List<Film> getPopularFilms(int count) {
        if (count == 0) count = 10;

        return filmDao.getPopularFilms(count);
    }


    public Mpa getMpa(Long mpaId) {

        MpaDao mpaDao = filmDao.getMpaDao();
        if (mpaDao.checkMpaId(mpaId)) {
            return mpaDao.getMpaById(mpaId);
        } else {
            throw new NotFoundException("Genre with this ID not found");
        }
    }

    public List<Mpa> getAllMpa() {
        return filmDao.getMpaDao().getAllMpa();
    }

    public Genre getGenre(Long genreId) {
        GenreDao genreDao = filmDao.getGenreDao();
        if (genreDao.checkGenreId(genreId)) {
            return genreDao.getGenreById(genreId);
        } else {
            throw new NotFoundException("Genre with this ID not found");
        }
    }

    public List<Genre> getAllGenres() {
        return filmDao.getGenreDao().getAllGenres();
    }

}