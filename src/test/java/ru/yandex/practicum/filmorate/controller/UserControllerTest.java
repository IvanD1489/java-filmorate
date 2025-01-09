package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserControllerTest {

    Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    UserController userController = new UserController();

    @Test
    public void isCreatingAndGettingUser() throws ValidationException {

        User user = new User(1, "test@ya.ru", "testLogin", "Test user", "1997-08-21");
        List<User> users = new ArrayList<>();
        users.add(user);
        String toCheckWith = gson.toJson(users);

        userController.create(user);

        assertEquals(toCheckWith, gson.toJson(userController.getUsers()));
    }

    @Test
    public void isUpdatingUser() throws ValidationException {

        User user = new User(1, "test@ya.ru", "testLogin", "Test user", "1997-08-21");
        userController.create(user);

        user = new User(1, "test-changed@ya.ru", "testLogin", "Test user", "1997-08-21");
        userController.update(user);

        List<User> users = new ArrayList<>();
        users.add(user);
        String toCheckWith = gson.toJson(users);

        assertEquals(toCheckWith, gson.toJson(userController.getUsers()));
    }

    @Test
    public void isValidatingUsers() {
        boolean isError = false;
        User user = new User(1, "ya@ya.ru", "test wrong login", "Test user", "1997-08-21");
        try {
            userController.create(user);
        } catch (ValidationException e) {
            isError = true;
        }

        assertTrue(isError);
    }

    @Test
    public void isReplacingEmptyNameWithLogin() throws ValidationException {
        boolean isError = false;
        User user = new User(1, "ya@ya.ru", "testLogin", "", "1997-08-21");
        userController.create(user);

        assertEquals(userController.getUsers().getFirst().getName(), user.getLogin());
    }

}
