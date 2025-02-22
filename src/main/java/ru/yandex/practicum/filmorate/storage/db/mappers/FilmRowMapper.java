package ru.yandex.practicum.filmorate.storage.db.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getString("release_date"));
        film.setDuration(rs.getInt("duration"));

        Rating rating = new Rating();
        rating.setId(rs.getInt("rating_id"));
        rating.setName(rs.getString("rating_name"));
        film.setMpa(rating);

        String genresStr = rs.getString("genres");
        if(genresStr != null) {
            Set<Genre> genres = new HashSet<>();
            for (String genreStr : genresStr.split(",")) {
                Genre genre = new Genre();
                genre.setId(Integer.parseInt(genreStr));
                genre.setName(Genre.values().get(Integer.parseInt(genreStr) - 1).getName());
                genres.add(genre);
            }
            film.setGenres(genres);
        }

        return film;
    }
}
