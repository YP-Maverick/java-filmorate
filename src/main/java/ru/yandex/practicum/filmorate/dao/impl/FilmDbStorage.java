package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.mapper.ModelMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;
import java.util.stream.Collectors;


@SuppressWarnings("checkstyle:Regexp")
@AllArgsConstructor
@Repository
@Primary
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final ModelMapper mapper;

    @Override
    public Film create(Film film) {
        log.debug("Запрос создать новый фильм.");

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");
        Long filmId = simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue();
        return film.withId(filmId);
    }

    @Override
    public Film deleteFilm(Long id) {
        log.debug("Получен запрос удалить фильм с id {}.", id);

        Film film = getFilmById(id);

        String sql = "DELETE FROM films WHERE id = ?";
        jdbcTemplate.update(sql, id);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        log.debug("Запрос обновить фильм с id {}.", film.getId());

        String sql = "UPDATE films SET name = ?, description = ?, "
                + "release_date = ?, duration = ?, "
                + "rating_id = ? WHERE id = ?";

        int row = jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        if (row != 1) {
            log.error("Запрос обновить несуществующий фильм с id {}.", film.getId());
            throw new NotFoundException(String.format("Фильма с id %d не существует.", film.getId()));
        } else return film;
    }

    @Override
    public Film getFilmById(Long id) {
        log.debug("Запрос получить фильм по id {}.", id);

        String sql = "SELECT f.*, "
                + "rm.name AS rating_name "
                + "FROM films f "
                + "JOIN rating_MPA rm ON rm.ID = f.rating_id "
                + "WHERE f.id = ?;";
        List<Film> films = jdbcTemplate.query(sql, mapper::makeFilm, id);
        if (films.isEmpty()) {
            log.error("Запрос получить фильм по неверному id {}.", id);
            throw new NotFoundException(String.format("Фильма с id %d не существует.", id));
        } else return films.get(0);
    }

    @Override
    public List<Film> findAllFilms() {
        log.debug("Запрос получить список всех фильмов.");

        String sql = "SELECT f.*, "
                + "rm.name AS rating_name "
                + "FROM films f "
                + "JOIN rating_MPA rm ON rm.ID = f.rating_id ";
        return fillFilmsWithData(jdbcTemplate.query(sql, mapper::makeFilm));
    }

    @Override
    public boolean contains(Long id) {
        log.debug("Запрос проверить наличие в БД  фильма с id {}.", id);

        String sql = "SELECT COUNT(*) FROM films WHERE id = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count == 1;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        log.debug("Запрос пользователя с id {} добавить лайк фильму с id {}.", userId, filmId);
        String checkSql = "SELECT COUNT(*) FROM film_likes WHERE film_id = ? AND user_id = ?";
        int checkLikes = jdbcTemplate.queryForObject(checkSql, Integer.class, filmId, userId);

        if (checkLikes == 0) {
            String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
            jdbcTemplate.update(sql, filmId, userId);
            String filmLikeSql = "UPDATE films SET likes = likes + 1 WHERE id = ?";
            jdbcTemplate.update(filmLikeSql, filmId);
        } else {
            log.info("Пользователь с id {} уже поставил лайк фильму с id {}.", userId, filmId);
        }
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        log.debug("Запрос пользователя с id {} удалить лайк у фильма с id {}.", userId, filmId);

        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ? ";
        int row = jdbcTemplate.update(sql, filmId, userId);
        if (row == 1) {
            String filmLikeSql = "UPDATE films SET likes = likes - 1 WHERE id = ?";
            jdbcTemplate.update(filmLikeSql, filmId);
        }
    }

    @Override
    public List<Film> getTopFilms(Integer count, Integer genreId, String year) {
        log.debug("Получен запрос вывести список популярных фильмов");

        String baseSql = "SELECT DISTINCT f.*, "
                + "rm.name AS rating_name "
                + "FROM films f "
                + "JOIN rating_MPA rm ON rm.ID = f.rating_id "
                + "LEFT JOIN film_genres fg ON fg.film_id = f.id "
                + "WHERE (YEAR(f.release_date) = ?) %s (fg.genre_id = ?) "
                + "ORDER BY likes DESC LIMIT ?";

        List<Film> films;

        if (year == null && genreId == null) {
            String sql = "SELECT f.*, "
                    + "rm.name AS rating_name "
                    + "FROM films f "
                    + "JOIN rating_MPA rm ON rm.ID = f.rating_id "
                    + "ORDER BY likes DESC LIMIT ?";
            films = jdbcTemplate.query(sql, mapper::makeFilm, count);
        } else if (year == null || genreId == null) {
            String nonStrictSql = String.format(baseSql, "OR");
            films = jdbcTemplate.query(nonStrictSql, mapper::makeFilm, year, genreId, count);
        } else {
            String strictSql = String.format(baseSql, "AND");
            films = jdbcTemplate.query(strictSql, mapper::makeFilm, year, genreId, count);
        }

        return fillFilmsWithData(films);
    }

    @Override
    public List<Film> getFilmsByDirector(Long directorId, String sortBy) {
        String baseSql = "SELECT f.*, "
                + "rm.name AS rating_name "
                + "FROM films f "
                + "JOIN rating_MPA rm ON rm.ID = f.rating_id "
                + "JOIN film_directors fd ON fd.film_id = f.id "
                + "WHERE fd.DIRECTOR_ID = ? ";
        switch (sortBy) {
            case "likes":
                String likesSql = baseSql + "ORDER BY f.likes DESC";
                return fillFilmsWithData(jdbcTemplate.query(likesSql, mapper::makeFilm, directorId));
            case "year":
                String yearSql = baseSql + "ORDER BY f.release_date";
                return fillFilmsWithData(jdbcTemplate.query(yearSql, mapper::makeFilm, directorId));
            default:
                return fillFilmsWithData(jdbcTemplate.query(baseSql, mapper::makeFilm, directorId));
        }
    }

    @Override
    public List<Film> getRecommendations(Long userId) {
        log.debug("Рекомендации фильмов для пользователя с id {} .", userId);

        String sql = "SELECT f.*, "
                + "rm.name AS rating_name "
                + "FROM films f "
                + "JOIN rating_MPA rm ON rm.ID = f.rating_id "
                + "JOIN film_likes fl ON f.id = fl.film_id "
                + "WHERE f.id NOT IN (SELECT film_id FROM film_likes WHERE user_id = ?) "
                + "AND fl.user_id IN (SELECT user_id FROM film_likes "
                + "WHERE film_id IN (SELECT film_id FROM film_likes WHERE user_id = ?) "
                + "GROUP BY user_id "
                + "ORDER BY COUNT(film_id) DESC LIMIT 10)"
                + "GROUP BY f.id";
        return fillFilmsWithData(jdbcTemplate.query(sql, mapper::makeFilm, userId, userId));
    }

    @Override
    public List<Film> getFilmsBySearch(String query, String by) {
        String baseSql = "SELECT f.*, "
                + "rm.name AS rating_name "
                + "FROM films f "
                + "JOIN rating_MPA rm ON rm.ID = f.rating_id ";
        String joinDirectorSql = "LEFT JOIN film_directors fd ON fd.film_id = f.id " +
                "LEFT JOIN directors d ON fd.director_id = d.id ";
        String sql;
        String parameter = "%" + query.toLowerCase() + "%";

        switch (by) {
            case "title":
                sql = baseSql + "WHERE LOWER(f.name) LIKE ? ORDER BY f.id DESC";
                return fillFilmsWithData(jdbcTemplate.query(sql, mapper::makeFilm, parameter));
            case "director":
                sql = baseSql + joinDirectorSql + "WHERE LOWER(d.name) LIKE ? ORDER BY f.id DESC";
                return fillFilmsWithData(jdbcTemplate.query(sql, mapper::makeFilm, parameter));
            default:
                sql = baseSql + joinDirectorSql +
                        "WHERE LOWER(d.name) LIKE ? OR LOWER(f.name) LIKE ? ORDER BY f.id DESC";
                return fillFilmsWithData(jdbcTemplate.query(sql, mapper::makeFilm, parameter, parameter));
        }
    }

    @Override
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        log.debug("Получен запрос вывести список общих фильмов пользователя с id {} и пользователя с id {} отсортированных по популярности", userId, friendId);

        String sql = "SELECT f.*, "
                + "rm.name AS rating_name "
                + "FROM films f "
                + "JOIN rating_MPA rm ON rm.ID = f.rating_id "
                + "WHERE f.id IN ( "
                + "    SELECT fl1.film_id "
                + "    FROM film_likes fl1 "
                + "    JOIN film_likes fl2 ON fl1.film_id = fl2.film_id "
                + "    WHERE fl1.user_id = ? AND fl2.user_id = ? "
                + ") "
                + "ORDER BY f.likes DESC";

        return fillFilmsWithData(jdbcTemplate.query(sql, mapper::makeFilm, userId, friendId));
    }


    private Map<Long, List<Genre>> associateGenresWithFilms(Set<Long> filmIds) {
        String sql = "SELECT genres.id, genres.name, fg.film_id FROM genres " +
                     "LEFT JOIN film_genres fg ON genres.id = fg.genre_id " +
                     "WHERE fg.film_id IN (:filmIds)";

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        MapSqlParameterSource parameters = new
                MapSqlParameterSource("filmIds", filmIds);

        return namedParameterJdbcTemplate.query(sql, parameters, mapper::mapGenres);
    }

    private Map<Long, List<Director>> associateDirectorsWithFilms(Set<Long> filmIds) {
        String sql = "SELECT directors.id, directors.name, fd.film_id FROM directors " +
                "LEFT JOIN film_directors fd ON directors.id = fd.director_id " +
                "WHERE fd.film_id IN (:filmIds)";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        MapSqlParameterSource parameters = new MapSqlParameterSource("filmIds", filmIds);
        return namedParameterJdbcTemplate.query(sql, parameters, mapper::mapDirectors);
    }

    private List<Film> fillFilmsWithData(List<Film> films) {
        Set<Long> filmIds = films.stream()
                .map(Film::getId)
                .collect(Collectors.toSet());

        Map<Long, List<Genre>> filmGenresMap = associateGenresWithFilms(filmIds);
        Map<Long, List<Director>> filmDirectorsMap = associateDirectorsWithFilms(filmIds);

        return films.stream().map(film -> {
            List<Genre> genres = filmGenresMap.getOrDefault(film.getId(), new ArrayList<>());
            film = film.withGenres(new HashSet<>(genres));

            List<Director> directors = filmDirectorsMap.getOrDefault(film.getId(), new ArrayList<>());
            film = film.withDirectors(new HashSet<>(directors));

            return film;
        }).collect(Collectors.toList());
    }

}