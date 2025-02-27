package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.db.mappers.FilmRowMapper;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Repository
@Primary
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setString(3, film.getReleaseDate());
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        updateGenres(film);

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        updateGenres(film);
        return film;
    }


    @Override
    public Film getFilmById(long id) {
        String sql = "select f.*, r.name as rating_name, listagg(distinct g.id) as genres, " +
                "count(distinct l.user_id) as likes_count from films f " +
                "left join films_by_genres fbg on fbg.film_id = f.id " +
                "left join genres g on g.id = fbg.genre_id " +
                "join ratings r on r.id = f.rating_id " +
                "left join likes l on l.film_id = f.id " +
                "where f.id = ? " +
                "group by f.id, f.name, f.description, f.release_date, f.duration, f.rating_id, r.name";
        List<Film> films = jdbcTemplate.query(sql, new FilmRowMapper(), id);
        if (films.isEmpty()) {
            return null;
        }
        return films.getFirst();
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "select f.*, r.name as rating_name, listagg(distinct g.id) as genres, " +
                "count(distinct l.user_id) as likes_count from films f " +
                "left join films_by_genres fbg on fbg.film_id = f.id " +
                "left join genres g on g.id = fbg.genre_id " +
                "join ratings r on r.id = f.rating_id " +
                "left join likes l on l.film_id = f.id " +
                "group by f.id, f.name, f.description, f.release_date, f.duration, f.rating_id, r.name";
        return jdbcTemplate.query(sql, new FilmRowMapper());
    }

    @Override
    public void deleteFilm(long id) {
        String sql = "DELETE FROM films_by_genres WHERE film_id = ?";
        jdbcTemplate.update(sql, id);
        sql = "DELETE FROM likes WHERE film_id = ?";
        jdbcTemplate.update(sql, id);
        sql = "DELETE FROM films WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void deleteAllFilms() {
        String sql = "DELETE FROM films_by_genres";
        jdbcTemplate.update(sql);
        sql = "DELETE FROM likes";
        jdbcTemplate.update(sql);
        sql = "DELETE FROM films";
        jdbcTemplate.update(sql);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public List<Film> getTopFilms(int count) {
        String sql = "select f.*, r.name as rating_name, listagg(distinct g.id) as genres, " +
                "count(distinct l.user_id) as likes_count from films f " +
                "left join films_by_genres fbg on fbg.film_id = f.id " +
                "left join genres g on g.id = fbg.genre_id " +
                "join ratings r on r.id = f.rating_id " +
                "left join likes l on l.film_id = f.id " +
                "group by f.id, f.name, f.description, f.release_date, f.duration, f.rating_id, r.name " +
                "order by likes_count desc " +
                "limit ?";
        List<Film> films = jdbcTemplate.query(sql, new FilmRowMapper(), count);
        return films;
    }

    private void updateGenres(Film film) {
        jdbcTemplate.update("DELETE FROM films_by_genres WHERE film_id = ?", film.getId());
        Set<Genre> genreObjects = film.getGenres();
        if (genreObjects != null && !genreObjects.isEmpty()) {
            Map<Long, Integer> params = new HashMap<>();
            String sql = "INSERT INTO films_by_genres (film_id, genre_id) VALUES (?, ?);";
            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {

                    Genre genre = (Genre) genreObjects.toArray()[i];
                    ps.setLong(1, film.getId());
                    ps.setInt(2, genre.getId());

                }

                @Override
                public int getBatchSize() {
                    return genreObjects.size();
                }
            });
        }
    }

}