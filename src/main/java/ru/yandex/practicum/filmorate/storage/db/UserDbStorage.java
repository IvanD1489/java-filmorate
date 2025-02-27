package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.db.mappers.UserRowMapper;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Repository
@Primary
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setString(4, user.getBirthday());
            return ps;
        }, keyHolder);

        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public User getUserById(long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> users = jdbcTemplate.query(sql, new UserRowMapper(), id);
        if (users.isEmpty()) {
            return null;
        }
        return users.getFirst();
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, new UserRowMapper());
    }

    @Override
    public void deleteUser(long id) {
        String sql = "DELETE FROM friends WHERE user_id = ?";
        jdbcTemplate.update(sql, id);
        sql = "DELETE FROM friends WHERE friend_id = ?";
        jdbcTemplate.update(sql, id);
        sql = "DELETE FROM likes WHERE user_id = ?";
        jdbcTemplate.update(sql, id);
        sql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void deleteAllUsers() {
        String sql = "DELETE FROM friends";
        jdbcTemplate.update(sql);
        sql = "DELETE FROM likes";
        jdbcTemplate.update(sql);
        sql = "DELETE FROM users";
        jdbcTemplate.update(sql);
    }

    @Override
    public void addFriend(long userId, long friendId) {
        String sql = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        String sql = "DELETE FROM friends WHERE user_id = ? and friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherUserId) {
        String sql = "SELECT u.* " +
                "FROM users u " +
                "JOIN friends f1 ON u.id = f1.friend_id " +
                "JOIN friends f2 ON f1.user_id = ? AND f2.user_id = ? AND f1.friend_id = f2.friend_id";
        return jdbcTemplate.query(sql, new UserRowMapper(), userId, otherUserId);
    }

    @Override
    public List<User> getFriends(long id) {
        String sql = "SELECT u.* " +
                "FROM users u " +
                "JOIN friends f ON u.id = f.friend_id " +
                "WHERE f.user_id = ?";
        return jdbcTemplate.query(sql, new UserRowMapper(), id);
    }

}
