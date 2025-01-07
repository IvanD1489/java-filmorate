package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public List<User> getUsers() {
        log.info("Пользователь запросил список всех пользователей.");
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@RequestBody User user) throws ValidationException {
        if (user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            String err = "Почта не может быть пустой и должна содержать символ @";
            log.error("При создании пользователя возникла ошибка: {}. Указанная почта: {}", err, user.getEmail());
            throw new ValidationException("Почта не может быть пустой и должна содержать символ @");
        }
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

        user.setId(getNextId());
        users.put(user.getId(), user);

        log.info("Пользователь cоздал нового пользователя. Ему присвоен идентификатор: {}", user.getId());

        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) throws ValidationException {
        if (user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            String err = "Почта не может быть пустой и должна содержать символ @";
            log.error("При обновлении пользователя возникла ошибка: {}. Указанная почта: {}", err, user.getEmail());
            throw new ValidationException("Почта не может быть пустой и должна содержать символ @");
        }
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

        User userToUpdate = users.get(user.getId());
        if (userToUpdate == null) {
            throw new ValidationException("Пользователь не найден");
        }

        userToUpdate.setName(user.getName());
        userToUpdate.setBirthday(user.getBirthday());
        userToUpdate.setEmail(user.getEmail());
        userToUpdate.setLogin(user.getLogin());

        log.info("Пользователь обновил запись с идентификатором {}", user.getId());

        return userToUpdate;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
