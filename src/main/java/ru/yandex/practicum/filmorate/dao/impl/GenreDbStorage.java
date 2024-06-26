package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.dao.mapper.ModelMapper;
import ru.yandex.practicum.filmorate.exception.GenreException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Repository
@Primary
@Slf4j
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private final ModelMapper mapper;

    @Override
    public Genre getGenreById(int id) {
        log.debug("Запрос получить жанр по id {}", id);

        String sql = "SELECT * FROM genres WHERE id = ?";
        List<Genre> genre = jdbcTemplate.query(sql, mapper::makeGenre, id);
        if (genre.isEmpty()) {
            log.error("Запрос получить жанр по неверному id {}.", id);
            throw new NotFoundException(String.format("Жанра с id %d не существует.", id));
        } else return genre.get(0);
    }

    @Override
    public List<Genre> getAllGenres() {
        log.debug("Запрос получить список всех жанров");

        String sql = "SELECT * FROM genres GROUP BY id";
        return jdbcTemplate.query(sql, mapper::makeGenre);
    }

    @Override
    public Set<Genre> getFilmGenres(Long filmId) {
        log.debug("Запрос получить список жанров фильма с id {}", filmId);

        String sql = "SELECT * FROM genres WHERE id IN "
                + "(SELECT genre_id FROM film_genres WHERE film_id = ?)";
        return new HashSet<>(jdbcTemplate.query(sql, mapper::makeGenre, filmId));
    }

    @Override
    public Set<Genre> addFilmGenres(Long filmId, Set<Genre> genres) {
        log.debug("Запрос добавить жанры фильма с id {}", filmId);

        List<Genre> filmGenres = new ArrayList<>(genres);
        String sql = "INSERT INTO film_genres (film_id, genre_id)"
                + "VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, filmId);
                ps.setLong(2, filmGenres.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return filmGenres.size();
            }
        });
        return genres;
    }

    @Override
    public Set<Genre> updateFilmGenres(Long filmId, Set<Genre> genres) {
        log.debug("Запрос обновить список жанров фильма с id {}", filmId);

        // Удаление прежнего списка жанров
        String delSql = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(delSql, filmId);

        // Добавление нового списка жанров
        return addFilmGenres(filmId, genres);
    }

    @Override
    public void checkGenres(Set<Genre> genres) {
        log.debug("Запрос проверить наличие жанров.");

        List<Integer> genreIdList = genres.stream()
                .map(Genre::getId)
                .collect(Collectors.toList());
        String inSql = String.join(",", Collections.nCopies(genreIdList.size(), "?"));

        List<Integer> genreDbIdList = jdbcTemplate.query(String.format("SELECT id FROM genres WHERE id IN (%s)", inSql),
                genreIdList.toArray(), (rs, rowNum) -> rs.getInt("id"));
        if (genreDbIdList.size() < genreIdList.size()) {
            genreDbIdList.forEach(genreIdList::remove);
            Integer genreId = genreIdList.stream().findFirst().get();
            log.error("Жанра с id {} нет в БД.", genreId);
            throw new GenreException(String.format("Неверно указан id (%d) жанра.",  genreId));
        }
    }
}