package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Qualifier("userDbStorage")
public class UserService {

    private final UserStorage userStorage;

    public void addFriend(long userId, long friendId) throws ResourceNotFoundException, ValidationException {
        if (userId == friendId) {
            log.error("Нельзя добавить в друзья самого себя");
            throw new ValidationException("Нельзя добавить в друзья самого себя");
        }
        if (userStorage.getUserById(userId) == null) {
            log.error("Пользователь с идентификатором {} не найден.", userId);
            throw new ResourceNotFoundException("Пользователь не найден");
        }
        if (userStorage.getUserById(friendId) == null) {
            log.error("Пользователь с идентификатором {} не найден.", friendId);
            throw new ResourceNotFoundException("Пользователь не найден");
        }
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(long userId, long friendId) throws ResourceNotFoundException {
        if (userStorage.getUserById(userId) == null) {
            log.error("Пользователь с идентификатором {} не найден.", userId);
            throw new ResourceNotFoundException("Пользователь не найден");
        }
        if (userStorage.getUserById(friendId) == null) {
            log.error("Пользователь с идентификатором {} не найден.", friendId);
            throw new ResourceNotFoundException("Пользователь не найден");
        }
        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getCommonFriends(long userId, long otherUserId) throws ResourceNotFoundException {

        if (userStorage.getUserById(userId) == null) {
            log.error("Пользователь с идентификатором {} не найден.", userId);
            throw new ResourceNotFoundException("Пользователь не найден");
        }
        if (userStorage.getUserById(otherUserId) == null) {
            log.error("Пользователь с идентификатором {} не найден.", otherUserId);
            throw new ResourceNotFoundException("Пользователь не найден");
        }

        return userStorage.getCommonFriends(userId, otherUserId);
    }

    public List<User> getFriends(long id) throws ResourceNotFoundException {
        if (userStorage.getUserById(id) == null) {
            log.error("Пользователь с идентификатором {} не найден.", id);
            throw new ResourceNotFoundException("Пользователь не найден");
        }
        return userStorage.getFriends(id);
    }

    public void deleteAllUsers() {
        userStorage.deleteAllUsers();
    }

    public User addUser(User user) throws ValidationException {
        if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            String err = "Логин не может быть пустой и не может содержать пробелов";
            log.error("При создании пользователя возникла ошибка: {}. Указанный логин: {}", err, user.getLogin());
            throw new ValidationException("Логин не может быть пустой и не может содержать пробелов");
        }
        if (LocalDate.parse(user.getBirthday()).isAfter(LocalDate.now())) {
            String err = "Дата рождения не может быть в будущем";
            log.error("При создании пользователя возникла ошибка: {}. Указанная дата: {}", err, user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

        if (user.getName() == null || user.getName().isEmpty()) {
            log.warn("При создании пользователя с логином {} не указано отображаемое имя, используем логин", user.getLogin());
            user.setName(user.getLogin());
        }

        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        return userStorage.addUser(user);
    }

    public User updateUser(User user) throws ResourceNotFoundException, ValidationException {
        if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            String err = "Логин не может быть пустой и не может содержать пробелов";
            log.error("При обновлении пользователя возникла ошибка: {}. Указанный логин: {}", err, user.getLogin());
            throw new ValidationException("Логин не может быть пустой и не может содержать пробелов");
        }
        if (LocalDate.parse(user.getBirthday()).isAfter(LocalDate.now())) {
            String err = "Дата рождения не может быть в будущем";
            log.error("При обновлении пользователя возникла ошибка: {}. Указанная дата: {}", err, user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            log.warn("При обновлении пользователя с логином {} не указано отображаемое имя, используем логин", user.getLogin());
            user.setName(user.getLogin());
        }

        if (userStorage.getUserById(user.getId()) == null) {
            log.error("Пользователь с идентификатором {} не найден.", user.getId());
            throw new ResourceNotFoundException("Пользователь не найден");
        }

        return userStorage.updateUser(user);
    }

    public User getUserById(long id) throws ResourceNotFoundException {
        User user = userStorage.getUserById(id);
        if (user == null) {
            log.error("Пользователь с идентификатором {} не найден.", id);
            throw new ResourceNotFoundException("Пользователь с идентификатором " + id + " не найден");
        }
        return user;
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void deleteUser(long id) {
        userStorage.deleteUser(id);
    }

}