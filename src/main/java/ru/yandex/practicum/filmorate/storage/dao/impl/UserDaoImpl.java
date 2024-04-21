package ru.yandex.practicum.filmorate.storage.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.UserDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class UserDaoImpl implements UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = User.builder()
                .id(rs.getLong("id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();

        //Получение списка ID друзей для пользователя
        String sql = "SELECT DISTINCT friend_id FROM friendship WHERE user_id = ?";
        List<Long> friendsIds = jdbcTemplate.query(sql, (rs1, rowNum1) -> rs1.getLong(1), user.getId());

        log.warn(user.getId().toString());
        log.warn(friendsIds.toString());

        user.getFriendsId().addAll(friendsIds);

        return user;
    };

    @Override
    public User createUser(User user) {
        //String sql = "INSERT INTO users (email, name, login, birthday) VALUES (?, ?, ?, ?)";
        //jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("email", user.getEmail());
        parameters.put("login", user.getLogin());
        parameters.put("name", user.getName());
        parameters.put("birthday", user.getBirthday());

        Number generatedId = jdbcInsert.executeAndReturnKey(parameters);

        user.setId((Long) generatedId);
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sql = "UPDATE users SET email= ?, name = ?, login  = ?, birthday = ? WHERE id = ?";

        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());

        return user;
    }

    @Override
    public void deleteUser(Long userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sql, userId);
        sql = "DELETE FROM friendship WHERE user_id = ? OR friend_id = ?";
        jdbcTemplate.update(sql, userId, userId);
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";


        return jdbcTemplate.query(sql, userRowMapper);
    }

    @Override
    public User getUserById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper, id);

        return users.stream().findFirst().orElse(null);
    }

    @Override
    public Boolean checkId(Long id) {
        String sql = "SELECT id FROM users WHERE id = ?";
        try {
            jdbcTemplate.queryForObject(sql, Long.class, id);
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
        return true;
    }

    @Override
    public User addFriend(Long userId, Long friendId) {

        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("friendship");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("user_id", userId);
        parameters.put("friend_id", friendId);

        jdbcInsert.execute(parameters);

        String checkSql = "SELECT * FROM users WHERE id = ?";
        List<User> users = jdbcTemplate.query(checkSql, userRowMapper, friendId);

        log.warn(userId.toString() + " userID");
        log.warn(friendId.toString() + " friendID");

        return users.stream().findFirst().orElse(null);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        //String sql = "DELETE FROM friendship WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)";

        String sql = "DELETE FROM friendship WHERE (user_id = ? AND friend_id = ?)";
        jdbcTemplate.update(sql, userId, friendId);

    }

    @Override
    public List<User> getFriends(Long userId) {
        String sql = "SELECT * FROM users JOIN friendship ON id = friend_id WHERE user_id = ?";
        return jdbcTemplate.query(sql, userRowMapper, userId);
    }

    @Override
    public List<User> getCommonsFriends(Long userId, Long otherId) {
        String sql = "SELECT u.* FROM users u JOIN friendship f1 ON u.id = f1.friend_id " +
                "JOIN friendship f2 ON u.id = f2.friend_id WHERE f1.user_id = ? AND f2.user_id = ?";
        return jdbcTemplate.query(sql, userRowMapper, userId, otherId);
    }
}