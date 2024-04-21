package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.MpaDao;

import java.sql.ResultSet;
import java.util.List;

@Component
public class MpaDaoImpl implements MpaDao {

    private JdbcTemplate jdbcTemplate;

    public MpaDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Mpa> mapRowMapper = (ResultSet rs, int rowNum) -> {
        Mpa mpa = new Mpa();
        mpa.setId(rs.getLong("mpa_id"));
        mpa.setName(rs.getString("name"));

        return mpa;
    };

    @Override
    public Mpa getMpaById(Long mpaId) {
        String sql = "SELECT * FROM mpa WHERE mpa_id = ?";
        return jdbcTemplate.queryForObject(sql, mapRowMapper, mpaId);
    }

    @Override
    public Mpa getMpaByFilmId(Long filmId) {
        String sql = "SELECT m.mpa_id, m.name FROM film AS f " +
                "JOIN mpa AS m ON m.mpa_id = f.mpa_id " +
                "WHERE f.film_id = ?";
        Mpa res;
        try {
            res = jdbcTemplate.queryForObject(sql, mapRowMapper, filmId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        return res;
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sql = "SELECT * FROM mpa";
        return jdbcTemplate.query(sql, mapRowMapper);
    }

    @Override
    public Boolean checkMpaId(Long id) {
        String sql = "SELECT mpa_id FROM mpa WHERE mpa_id = ?";
        try {
            jdbcTemplate.queryForObject(sql, Long.class, id);
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
        return true;
    }

}
