package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private static long idCounter = 1;

    private final UserStorage userStorage;

    // Cоздание пользователя
    public User createUser(User user) {

        replaceNameWithLogin(user);
        user.setId(generateId());
        userStorage.add(user); //
        return user;
    }

    // Обновление пользователя;
    public User updateUser(User user) {
        replaceNameWithLogin(user);
        if (user.getId() != null) {
            boolean isExist = userStorage.getAllUsers().stream().anyMatch(existingUser -> Objects.equals(existingUser.getId(), user.getId()));
            if (!isExist) {
                throw new NotFoundException("User with this ID not found");
            }
            userStorage.getAllUsers().removeIf(existingUser -> Objects.equals(existingUser.getId(), user.getId()));
            userStorage.add(user);
        } else {
            user.setId(generateId());
            userStorage.add(user);
        }
        return user;
    }

    // Получение списка всех пользователей.
    public List<User> getAllUsers() {
        return new ArrayList<>(userStorage.getAllUsers());
    }
    //----------------------------------------------
    // Операции с друзьями

    // Добавление в друзья,
    public User addFriend(Long userId, Long friendId) {

        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (user == null) {
            throw new NotFoundException("User with this ID not found");
        }

        if (friend == null) {
            throw new NotFoundException("Friend with this ID not found");
        }

        user.getFriendsId().add(friendId);
        friend.getFriendsId().add(userId);

        return user;
    }

    // Удаление из друзей,
    public User deleteFriend(Long userId, Long friendId) {

        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (user == null) {
            throw new NotFoundException("User with this ID not found");
        }

        if (friend == null) {
            throw new NotFoundException("Friend with this ID not found");
        }

        user.getFriendsId().remove(friendId);
        friend.getFriendsId().remove(userId);

        return user;
    }

    // Получение всех друзей пользователя
    public List<User> getFriends(Long userId) {
        return userStorage.getAllUsers()
                .stream()
                .filter(u -> userStorage.getUserById(userId).getFriendsId().contains(u.getId()))
                .collect(Collectors.toList());
    }

    // Получение списка общих друзей
    public List<User> getCommonsFriends(Long userId, Long otherUserId) {

        User user = userStorage.getUserById(userId);
        User otherUser = userStorage.getUserById(otherUserId);
        if (user == null) {
            throw new NotFoundException("User with this ID not found");
        }

        if (otherUser == null) {
            throw new NotFoundException("OtherUser with this ID not found");
        }

        Set<Long> intersection = new HashSet<>(user.getFriendsId());
        intersection.retainAll(otherUser.getFriendsId());

        return intersection
                .stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    //-------------------------------------------------------------------------
    // Дополнительные методы
    public void replaceNameWithLogin(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
    }

    private long generateId() {
        return idCounter++;
    }
}
