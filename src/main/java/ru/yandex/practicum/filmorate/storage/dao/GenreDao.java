package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreDao {


    void saveGenresByFilmId(Long filmId, List<Genre> genres);

    List<Genre> getGenresByFilmId(Long filmId);

    Boolean checkGenreId(Long id);

    boolean checkAllGenresExist(List<Long> genreIds);

    Genre getGenreById(Long genreId);

    List<Genre> getAllGenres();
}
