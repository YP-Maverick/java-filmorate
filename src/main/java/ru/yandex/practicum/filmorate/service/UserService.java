package ru.yandex.practicum.filmorate.service;


import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.UserDao;

import java.util.*;

@Service
public class UserService {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    // Cоздание пользователя
    public User createUser(User user) {

        replaceNameWithLogin(user);
        return  userDao.createUser(user);
    }

    // Обновление пользователя;
    public User updateUser(User user) {
        replaceNameWithLogin(user);

        if (userDao.checkId(user.getId())) {
            return userDao.updateUser(user);
        } else {
            throw new NotFoundException("User with this ID not found");
        }
    }

    // Получение списка всех пользователей.
    public List<User> getAllUsers() {
        return new ArrayList<>(userDao.getAllUsers());
    }
    //----------------------------------------------
    // Операции с друзьями

    // Добавление в друзья,
    public User addFriend(Long userId, Long friendId) {

        if (userDao.checkId(userId) && userDao.checkId(friendId)) {
            return userDao.addFriend(userId, friendId);
        } else {
            throw new NotFoundException("User with this ID not found");
        }
    }

    // Удаление из друзей,
    public User deleteFriend(Long userId, Long friendId) {

        if (userDao.checkId(userId) && userDao.checkId(friendId)) {
            userDao.deleteFriend(userId, friendId);
            return userDao.getUserById(userId);
        } else {
            throw new NotFoundException("User with this ID not found");
        }

    }

    // Получение всех друзей пользователя
    public List<User> getFriends(Long userId) {

        if (userDao.checkId(userId)) {
            return userDao.getFriends(userId);
        } else {
            throw new NotFoundException("User with this ID not found");
        }
    }

    // Получение списка общих друзей
    public List<User> getCommonsFriends(Long userId, Long otherUserId) {

        if (userDao.checkId(userId) && userDao.checkId(otherUserId)) {
            userDao.deleteFriend(userId, otherUserId);
            return userDao.getCommonsFriends(userId, otherUserId);
        } else {
            throw new NotFoundException("User with this ID not found");
        }
    }

    //-------------------------------------------------------------------------
    // Дополнительные методы
    public void replaceNameWithLogin(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
    }
}
