package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User addUser(User user);

    User updateUser(User user);

    User getUserById(long id);

    List<User> getAllUsers();

    void deleteUser(long id);

    void deleteAllUsers();

    void addFriend(long userId, long friendId);

    void removeFriend(long userId, long friendId);

    List<User> getCommonFriends(long userId, long otherUserId);

    List<User> getFriends(long id);

}