package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserStorage userStorage;

    @Autowired
    private UserService userService;

    @Autowired
    public UserController(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Пользователь запросил список всех пользователей.");
        return userStorage.getAllUsers();
    }

    @PostMapping
    public User create(@RequestBody @Valid User user) throws ValidationException {
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

        userStorage.addUser(user);
        log.info("Пользователь cоздал нового пользователя. Ему присвоен идентификатор: {}", user.getId());
        return user;
    }

    @PutMapping
    public User update(@RequestBody @Valid User user) throws ValidationException, ResourceNotFoundException {
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

        log.info("Пользователь обновил запись с идентификатором {}", user.getId());

        return userStorage.updateUser(user);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable long id) throws ResourceNotFoundException {
        User user = userStorage.getUserById(id);
        if (user != null) {
            log.info("Пользователь с идентификатором {} найден.", id);
            return user;
        } else {
            log.error("Пользователь с идентификатором {} не найден.", id);
            throw new ResourceNotFoundException("Пользователь с идентификатором " + id + " не найден");
        }
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        userStorage.deleteUser(id);
        log.info("Пользователь с идентификатором {} удалён.", id);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addFriend(@PathVariable long userId, @PathVariable long friendId) throws ResourceNotFoundException {
        if (userStorage.getUserById(userId) == null) {
            log.error("Пользователь с идентификатором {} не найден.", userId);
            throw new ResourceNotFoundException("Пользователь не найден");
        }
        if (userStorage.getUserById(friendId) == null) {
            log.error("Пользователь с идентификатором {} не найден.", friendId);
            throw new ResourceNotFoundException("Пользователь не найден");
        }
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFriend(@PathVariable long userId, @PathVariable long friendId) throws ResourceNotFoundException {
        if (userStorage.getUserById(userId) == null) {
            log.error("Пользователь с идентификатором {} не найден.", userId);
            throw new ResourceNotFoundException("Пользователь не найден");
        }
        if (userStorage.getUserById(friendId) == null) {
            log.error("Пользователь с идентификатором {} не найден.", friendId);
            throw new ResourceNotFoundException("Пользователь не найден");
        }
        userService.removeFriend(userId, friendId);
    }

    @GetMapping("/{userId}/friends/common/{otherUserId}")
    public List<User> getCommonFriends(@PathVariable long userId, @PathVariable long otherUserId) throws ResourceNotFoundException {
        if (userStorage.getUserById(userId) == null) {
            log.error("Пользователь с идентификатором {} не найден.", userId);
            throw new ResourceNotFoundException("Пользователь не найден");
        }
        if (userStorage.getUserById(otherUserId) == null) {
            log.error("Пользователь с идентификатором {} не найден.", otherUserId);
            throw new ResourceNotFoundException("Пользователь не найден");
        }
        return userService.getCommonFriends(userId, otherUserId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable long id) throws ResourceNotFoundException {
        if (userStorage.getUserById(id) == null) {
            log.error("Пользователь с идентификатором {} не найден.", id);
            throw new ResourceNotFoundException("Пользователь не найден");
        }
        return userService.getFriends(id);
    }

    public void deleteAllUsers() {
        userService.deleteAllUsers();
    }


}
