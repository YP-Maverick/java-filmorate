package ru.yandex.practicum.filmorate.dao.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ModelMapper {

    public Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_Date").toLocalDate())
                .duration(rs.getInt("duration"))
                .likes(rs.getLong("likes"))
                .mpa(RatingMpa.builder()
                        .id(rs.getInt("rating_id"))
                        .name(rs.getString("rating_name"))
                        .build())
                .build();
    }

    public User makeUser(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getLong("id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }

    public Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .build();
    }

    public RatingMpa makeRatingMpa(ResultSet rs, int rowNum) throws SQLException {
        return RatingMpa.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .build();
    }

    public Event makeEvent(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder()
                .eventId(rs.getLong("event_id"))
                .entityId(rs.getLong("entity_id"))
                .userId(rs.getLong("user_id"))
                .timestamp(rs.getLong("event_timestamp"))
                .eventType(rs.getString("event_type"))
                .operation(rs.getString("operation"))
                .build();
    }

    public Review makeReview(ResultSet rs, int rowNum) throws SQLException {
        return Review.builder()
                .id(rs.getLong("id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .userId(rs.getLong("user_id"))
                .filmId(rs.getLong("film_id"))
                .useful(rs.getInt("useful"))
                .build();
    }

    public Director makeDirector(ResultSet rs, int rowNum) throws SQLException {
        return Director.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .build();
    }

    public Map<Long, List<Genre>> mapGenres(ResultSet rs) throws SQLException {
        Map<Long, List<Genre>> filmGenresMap = new HashMap<>();
        while (rs.next()) {
            Long filmId = rs.getLong("film_id");
            Genre genre = Genre.builder()
                    .id(rs.getInt("id"))
                    .name(rs.getString("name"))
                    .build();
            filmGenresMap.computeIfAbsent(filmId, k -> new ArrayList<>()).add(genre);
        }
        return filmGenresMap;
    }

    public Map<Long, List<Director>> mapDirectors(ResultSet rs) throws SQLException {
        Map<Long, List<Director>> filmDirectorsMap = new HashMap<>();
        while (rs.next()) {
            Long filmId = rs.getLong("film_id");
            Director director = Director.builder()
                    .id(rs.getLong("id"))
                    .name(rs.getString("name"))
                    .build();
            filmDirectorsMap.computeIfAbsent(filmId, k -> new ArrayList<>()).add(director);
        }
        return filmDirectorsMap;
    }
}
