package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUserById(long id) {
        return users.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteUser(long id) {
        users.remove(id);
    }

    @Override
    public void deleteAllUsers() {
        users.clear();
    }

    @Override
    public void addFriend(long userId, long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        updateUser(user);
        updateUser(friend);
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        updateUser(user);
        updateUser(friend);
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherUserId) {
        User user = getUserById(userId);
        User otherUser = getUserById(otherUserId);

        Set<Long> commonFriendIds = user.getFriends();
        commonFriendIds.retainAll(otherUser.getFriends());
        return commonFriendIds.stream()
                .map(this::getUserById)
                .toList();
    }

    @Override
    public List<User> getFriends(long id) {
        User user = getUserById(id);
        Set<Long> friendIds = user.getFriends();
        return friendIds.stream()
                .map(this::getUserById)
                .filter(Objects::nonNull)
                .toList();
    }

    private long getNextId() {
        return users.keySet().stream().mapToLong(id -> id).max().orElse(0) + 1;
    }
}