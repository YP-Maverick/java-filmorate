package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FriendsStorage;
import ru.yandex.practicum.filmorate.dao.mapper.ModelMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@AllArgsConstructor
@Repository
@Primary
@Slf4j
public class FriendsDbStorage implements FriendsStorage {
    private final JdbcTemplate jdbcTemplate;
    private final ModelMapper mapper;

    @Override
    public void addFriend(Long userId, Long friendId) {
        log.debug("Запрос от id {} добавить в друзья id {}.", userId, friendId);

        String sql = "INSERT INTO friends(user_id, friend_id, status) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, friendId, true);
    }

    @Override
    public long deleteFriend(Long userId, Long friendId) {
        log.debug("Запрос от id {} удалить из друзей id {}.", userId, friendId);

        String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id =?";
        jdbcTemplate.update(sql, userId, friendId);

        return friendId;
    }

    @Override
    public List<User> getAllFriends(Long id) {
        log.debug("Запрос от id {} получить список друзей.", id);

        String sql = "SELECT * FROM users "
                + "WHERE id IN "
                + "(SELECT f.friend_id FROM friends f "
                + "WHERE f.user_id = ?)";

        return jdbcTemplate.query(sql, mapper::makeUser, id);
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherId) {
        log.debug("Запрос от id {} получить список общих друзей с id {}.", userId, otherId);

        String sql = "SELECT * FROM users WHERE id IN "
                + "(SELECT f.friend_id FROM FRIENDS f "
                + "JOIN FRIENDS f2 ON f.friend_id = f2.FRIEND_ID "
                + "WHERE f2.USER_ID = ? "
                + "AND f.user_ID = ?)";

        return jdbcTemplate.query(sql, mapper::makeUser, userId, otherId);
    }
}