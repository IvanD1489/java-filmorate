package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> getUsers() {
        log.info("Пользователь запросил список всех пользователей.");
        return userService.getAllUsers();
    }

    @PostMapping
    public User create(@RequestBody @Valid User user) throws ValidationException {
        userService.addUser(user);
        log.info("Пользователь cоздал нового пользователя. Ему присвоен идентификатор: {}", user.getId());
        return user;
    }

    @PutMapping
    public User update(@RequestBody @Valid User user) throws ValidationException, ResourceNotFoundException {
        log.info("Пользователь обновил запись с идентификатором {}", user.getId());
        return userService.updateUser(user);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable long id) throws ResourceNotFoundException {
        return userService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
        log.info("Пользователь с идентификатором {} удалён.", id);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addFriend(@PathVariable long userId, @PathVariable long friendId) throws ResourceNotFoundException, ValidationException {
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFriend(@PathVariable long userId, @PathVariable long friendId) throws ResourceNotFoundException {
        userService.removeFriend(userId, friendId);
    }

    @GetMapping("/{userId}/friends/common/{otherUserId}")
    public List<User> getCommonFriends(@PathVariable long userId, @PathVariable long otherUserId) throws ResourceNotFoundException {
        return userService.getCommonFriends(userId, otherUserId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable long id) throws ResourceNotFoundException {
        return userService.getFriends(id);
    }

    public void deleteAllUsers() {
        userService.deleteAllUsers();
    }


}
