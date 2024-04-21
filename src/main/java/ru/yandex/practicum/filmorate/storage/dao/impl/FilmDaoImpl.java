package ru.yandex.practicum.filmorate.storage.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.FilmDao;
import ru.yandex.practicum.filmorate.storage.dao.GenreDao;
import ru.yandex.practicum.filmorate.storage.dao.MpaDao;

import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FilmDaoImpl implements FilmDao {


    private JdbcTemplate jdbcTemplate;
    private MpaDao mpaDao;
    private GenreDao genreDao;

    @Override
    public GenreDao getGenreDao() {
        return genreDao;
    }

    @Override
    public MpaDao getMpaDao() {
        return mpaDao;
    }

    @Autowired
    public FilmDaoImpl(JdbcTemplate jdbcTemplate, MpaDao mpaDao, GenreDao genreDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaDao = mpaDao;
        this.genreDao = genreDao;
    }

    private final RowMapper<Film> filmRowMapper = (rs, rowNum) -> {
        Long filmId = rs.getLong("film_id");

        // Тесты в Postman не пропускают список жанров без сортировки по id
        List<Genre> sortedGenres = genreDao.getGenresByFilmId(filmId).stream()
                .sorted(Comparator.comparing(Genre::getId))
                .collect(Collectors.toList());

        Film film = Film.builder()
                .id(filmId)
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(mpaDao.getMpaByFilmId(filmId))
                .genres(new HashSet<>(sortedGenres))
                .build();

        log.warn("ROW MAPPER " + film.toString());

        return film;
    };


    @Override
    public Film createFilm(Film film) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film")
                .usingGeneratedKeyColumns("film_id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", film.getName());
        parameters.put("description", film.getDescription());
        parameters.put("release_date", Date.valueOf(film.getReleaseDate()));
        parameters.put("duration", film.getDuration());
        parameters.put("mpa_id", film.getMpa().getId());

        Number generatedId = jdbcInsert.executeAndReturnKey(parameters);
        film.setId((Long) generatedId);

        log.warn("ID = " + generatedId + " " + film.toString());

        genreDao.saveGenresByFilmId((Long) generatedId, film.getGenres().stream().toList());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE film SET name = ?, description = ?, release_date = ?, duration = ? WHERE film_id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), Date.valueOf(film.getReleaseDate()), film.getDuration(), film.getId());
        return film;
    }

    @Override
    public void deleteFilm(Long filmId) {
        String deleteFilmSql = "DELETE FROM film WHERE film_id = ?";
        String deleteFilmGenresSql = "DELETE FROM film_genres WHERE film_id = ?";

        jdbcTemplate.update(deleteFilmGenresSql, filmId); // Удаление связанных записей в таблице film_genres
        jdbcTemplate.update(deleteFilmSql, filmId); // Удаление фильма
    }

    @Override
    public Film getFilmById(Long id) {
        String sql = "SELECT * FROM film WHERE film_id = ?";
        return jdbcTemplate.queryForObject(sql, filmRowMapper, id);
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT * FROM film";
        return jdbcTemplate.query(sql, filmRowMapper);
    }


    @Override
    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO film_likes (user_id, film_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, filmId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        String sql = "DELETE FROM film_likes WHERE user_id = ? AND film_id = ?";
        jdbcTemplate.update(sql, userId, filmId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = "SELECT f.film_id, f.name, f.mpa_id, f.description, f.release_date, f.duration " +
                "FROM film f " +
                "LEFT JOIN film_likes fl ON f.film_id = fl.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(fl.user_id) DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sql, filmRowMapper, count);
    }

    @Override
    public String getMpaByFilmId(Long filmId) {
        return null;
    }

    @Override
    public Boolean checkFilmId(Long film_id) {
        String sql = "SELECT film_id FROM film WHERE film_id = ?";
        try {
            jdbcTemplate.queryForObject(sql, Long.class, film_id);
        } catch (EmptyResultDataAccessException e) {
            log.warn("FALSE");
            return false;
        }
        return true;
    }

}