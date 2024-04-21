package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenreDao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Component
public class GenreDaoImpl implements GenreDao {

    private JdbcTemplate jdbcTemplate;

    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Genre> mapRowMapper = (ResultSet rs, int rowNum) -> {
        Genre genre = new Genre();
        genre.setId(rs.getLong("genre_id"));
        genre.setName(rs.getString("name"));

        return genre;
    };

    @Override
    public void saveGenresByFilmId(Long filmId, List<Genre> genres) {
        String sql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, filmId);
                ps.setLong(2, genres.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        });
    }

    @Override
    public List<Genre> getGenresByFilmId(Long filmId) {
        String sql = "SELECT g.genre_id, g.name " +
                     "FROM genre g " +
                     "INNER JOIN film_genre fg ON g.genre_id = fg.genre_id " +
                     "WHERE fg.film_id = ?;";
        List<Genre>  res;
        try {
            res = jdbcTemplate.query(sql, mapRowMapper, filmId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        return res;
    }

    @Override
    public Boolean checkGenreId(Long id) {
        String sql = "SELECT genre_id FROM genre WHERE genre_id = ?";
        try {
            jdbcTemplate.queryForObject(sql, Long.class, id);
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean checkAllGenresExist(List<Long> genreIds) {
        String sql = "SELECT COUNT(*) FROM genre WHERE genre_id IN (%s)";
        String inSql = String.join(",", Collections.nCopies(genreIds.size(), "?"));

        try {
            Long count = jdbcTemplate.queryForObject(
                    String.format(sql, inSql), genreIds.toArray(), Long.class);

            return count != null && count == genreIds.size();
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    @Override
    public Genre getGenreById(Long genreId) {
        String sql = "SELECT g.genre_id, g.name \n" +
                "FROM genre g \n" +
                "WHERE g.genre_id = ?;";
        Genre res;
        try {
            res = jdbcTemplate.queryForObject(sql, mapRowMapper, genreId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        return res;
    }

    @Override
    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM genre";
        return jdbcTemplate.query(sql, mapRowMapper);
    }

}