package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserDao {
    User createUser(User user);
    User updateUser(User user);
    //void deleteUser(Long userId);
    List<User> getAllUsers();
    User getUserById(Long id);
    User addFriend(Long userId, Long friendId);
    void deleteFriend(Long userId, Long friendId);
    List<User> getFriends(Long userId);
    List<User> getCommonsFriends(Long userId, Long otherId);

    Boolean checkId(Long id);
}