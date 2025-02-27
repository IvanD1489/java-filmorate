package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase
@SpringBootTest
public class UserControllerTest {

    Gson gson;

    @Autowired
    private UserController userController;

    @Autowired
    private FilmController filmController;


    @BeforeEach
    public void setUp() {
        gson = new GsonBuilder()
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create();

        userController.deleteAllUsers();
    }

    @Test
    public void isCreatingAndGettingUser() throws ValidationException, ResourceNotFoundException {

        User user = new User(1, "test@ya.ru", "testLogin", "Test user", "1997-08-21", new HashSet<>());
        userController.create(user);
        String toCheckWith = gson.toJson(userController.getUserById(user.getId()));

        assertEquals(toCheckWith, gson.toJson(userController.getUserById(user.getId())));
    }

    @Test
    public void isUpdatingUser() throws ValidationException, ResourceNotFoundException {

        User user = new User(1, "test@ya.ru", "testLogin", "Test user", "1997-08-21", new HashSet<>());
        userController.create(user);

        user = new User(user.getId(), "test-changed@ya.ru", "testLogin", "Test user", "1997-08-21", new HashSet<>());
        userController.update(user);

        List<User> users = new ArrayList<>();
        users.add(user);
        String toCheckWith = gson.toJson(users);

        assertEquals(toCheckWith, gson.toJson(userController.getUsers()));
    }

    @Test
    public void isValidatingUsers() {
        boolean isError = false;
        User user = new User(1, "ya@ya.ru", "test wrong login", "Test user", "1997-08-21", new HashSet<>());
        try {
            userController.create(user);
        } catch (ValidationException e) {
            isError = true;
        }

        assertTrue(isError);
    }

    @Test
    public void isReplacingEmptyNameWithLogin() throws ValidationException {
        User user = new User(1, "ya@ya.ru", "testLogin", "", "1997-08-21", new HashSet<>());
        userController.create(user);

        assertEquals(userController.getUsers().getFirst().getName(), user.getLogin());
    }

    @Test
    public void isDeletingUser() throws ValidationException {
        User user = new User(1, "test@ya.ru", "testLogin", "Test user", "1997-08-21", new HashSet<>());
        userController.create(user);

        userController.deleteUser(user.getId());

        List<User> usersAfterDeletion = userController.getUsers();
        assertFalse(usersAfterDeletion.contains(user));
    }

    @Test
    public void isAddingFriend() throws ResourceNotFoundException, ValidationException {
        User user = new User(1, "user@ya.ru", "userLogin", "User", "1997-08-21", new HashSet<>());
        User friend = new User(2, "friend@ya.ru", "friendLogin", "Friend", "1997-08-21", new HashSet<>());
        userController.create(user);
        userController.create(friend);

        userController.addFriend(user.getId(), friend.getId());

        List<User> friends = userController.getFriends(user.getId());
        assertTrue(friends.contains(friend));
    }

    @Test
    public void isAddingFriendWhenUserNotFound() throws ValidationException {
        User friend = new User(2, "friend@ya.ru", "friendLogin", "Friend", "1997-08-21", new HashSet<>());
        userController.create(friend);

        boolean isError = false;
        try {
            userController.addFriend(999, friend.getId());
        } catch (ResourceNotFoundException e) {
            isError = true;
        }

        assertTrue(isError);
    }

    @Test
    public void isRemovingFriend() throws ResourceNotFoundException, ValidationException {
        User user = new User(1, "user@ya.ru", "userLogin", "User", "1997-08-21", new HashSet<>());
        User friend = new User(2, "friend@ya.ru", "friendLogin", "Friend", "1997-08-21", new HashSet<>());
        userController.create(user);
        userController.create(friend);
        userController.addFriend(user.getId(), friend.getId());

        userController.removeFriend(user.getId(), friend.getId());

        List<User> friends = userController.getFriends(user.getId());
        assertFalse(friends.contains(friend));
    }

    @Test
    public void isGettingCommonFriends() throws ResourceNotFoundException, ValidationException {
        User user = new User(1, "user@ya.ru", "userLogin", "User", "1997-08-21", new HashSet<>());
        User otherUser = new User(2, "other@ya.ru", "otherLogin", "Other User", "1997-08-21", new HashSet<>());
        User commonFriend = new User(3, "common@ya.ru", "commonLogin", "Common Friend", "1997-08-21", new HashSet<>());

        userController.create(user);
        userController.create(otherUser);
        userController.create(commonFriend);

        userController.addFriend(user.getId(), commonFriend.getId());
        userController.addFriend(otherUser.getId(), commonFriend.getId());

        List<User> commonFriends = userController.getCommonFriends(user.getId(), otherUser.getId());

        assertTrue(commonFriends.contains(commonFriend));
    }

}
