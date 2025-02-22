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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase
@SpringBootTest
public class FilmControllerTest {

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

        filmController.deleteAllFilms();
    }


    @Test
    public void isCreatingAndGettingFilms() throws ValidationException, ResourceNotFoundException {
        Set<Genre> genres = new HashSet<>();
        genres.add(Genre.values().getFirst());
        Rating rating = Rating.values().getFirst();
        Film film = new Film(1, "Test name", "Test desc", "1997-08-21", 120, genres, rating);
        List<Film> films = new ArrayList<>();
        films.add(film);
        String toCheckWith = gson.toJson(films);

        filmController.create(film);

        assertEquals(toCheckWith, gson.toJson(filmController.getFilms()));
    }

    @Test
    public void isUpdatingFilm() throws ValidationException, ResourceNotFoundException {
        Set<Genre> genres = new HashSet<>();
        genres.add(Genre.values().getFirst());
        Rating rating = Rating.values().getFirst();
        Film film = new Film(1, "Test name", "Test desc", "1997-08-21", 120, genres, rating);
        filmController.create(film);

        film = new Film(film.getId(), "Test name updated", "Test desc updated", "2020-08-21", 150, genres, rating);
        filmController.update(film);

        String toCheckWith = gson.toJson(film);

        assertEquals(toCheckWith, gson.toJson(filmController.getFilmById(film.getId())));
    }

    @Test
    public void isValidatingFilms() {
        Set<Genre> genres = new HashSet<>();
        genres.add(Genre.values().getFirst());
        Rating rating = Rating.values().getFirst();
        boolean isError = false;
        Film film = new Film(1, "", "Test desc", "1997-08-21", 120, genres, rating);
        try {
            filmController.create(film);
        } catch (ValidationException e) {
            isError = true;
        } catch (ResourceNotFoundException e) {
            isError = true;
        }

        assertTrue(isError);
    }

    @Test
    public void isGettingTopFilms() throws ValidationException, ResourceNotFoundException {
        Set<Genre> genres = new HashSet<>();
        genres.add(Genre.values().getFirst());
        Rating rating = Rating.values().getFirst();
        Film film1 = new Film(1, "Film 1", "Description 1", "2020-01-01", 120, genres, rating);
        Film film2 = new Film(2, "Film 2", "Description 2", "2020-01-02", 150, genres, rating);
        Film film3 = new Film(3, "Film 3", "Description 3", "2020-01-03", 100, genres, rating);

        User user1 = new User(1, "test1@ya.ru", "testLogin1", "Test user1", "1997-08-21", null);
        userController.create(user1);
        User user2 = new User(2, "test2@ya.ru", "testLogin2", "Test user2", "1997-08-21", null);
        userController.create(user2);
        User user3 = new User(3, "test3@ya.ru", "testLogin3", "Test user3", "1997-08-21", null);
        userController.create(user3);

        filmController.create(film1);
        filmController.create(film2);
        filmController.create(film3);

        filmController.addLike(film1.getId(), user1.getId());
        filmController.addLike(film2.getId(), user1.getId());
        filmController.addLike(film2.getId(), user2.getId());
        filmController.addLike(film3.getId(), user2.getId());
        filmController.addLike(film3.getId(), user3.getId());


        List<Film> topFilms = filmController.getTopFilms(2);
        List<Film> expectedTopFilms = List.of(film3, film2);

        assertEquals(expectedTopFilms.size(), topFilms.size());
        assertTrue(topFilms.containsAll(expectedTopFilms));
    }

    @Test
    public void isRemovingLike() throws ValidationException, ResourceNotFoundException {
        Set<Genre> genres = new HashSet<>();
        genres.add(Genre.values().getFirst());
        Rating rating = Rating.values().getFirst();
        Film film = new Film(1, "Test Film", "Test desc", "2020-01-01", 120, genres, rating);
        filmController.create(film);
        Film film2 = new Film(2, "Test Film", "Test desc", "2020-01-01", 120, genres, rating);
        filmController.create(film2);

        User user = new User(1, "test@ya.ru", "testLogin", "Test user", "1997-08-21", null);
        userController.create(user);
        User user2 = new User(1, "test@ya.ru", "testLogin", "Test user", "1997-08-21", null);
        userController.create(user2);

        filmController.addLike(film.getId(), user.getId());
        filmController.addLike(film2.getId(), user.getId());
        filmController.addLike(film2.getId(), user2.getId());

        List<Film> topFilmsBeforeRemoval = filmController.getTopFilms(1);
        assertEquals(gson.toJson(film2), gson.toJson(topFilmsBeforeRemoval.getFirst()));

        filmController.removeLike(film2.getId(), user.getId());
        filmController.removeLike(film2.getId(), user2.getId());

        List<Film> topFilmsAfterRemoval = filmController.getTopFilms(1);
        assertEquals(gson.toJson(film), gson.toJson(topFilmsAfterRemoval.getFirst()));
    }

    @Test
    public void isDeletingFilm() throws ValidationException, ResourceNotFoundException {
        Set<Genre> genres = new HashSet<>();
        genres.add(Genre.values().getFirst());
        Rating rating = Rating.values().getFirst();
        Film film = new Film(1, "Film to delete", "Description", "2020-01-01", 120, genres, rating);
        filmController.create(film);

        assertNotNull(filmController.getFilmById(film.getId()));

        filmController.deleteFilm(film.getId());

        assertThrows(ResourceNotFoundException.class, () -> filmController.getFilmById(1));
    }

    @Test
    public void isValidatingAddLikeWithNonExistingFilm() {
        boolean isError = false;
        try {
            filmController.addLike(999, 1);
        } catch (ResourceNotFoundException e) {
            isError = true;
        }
        assertTrue(isError);
    }

    @Test
    public void isValidatingRemoveLikeWithNonExistingFilm() {
        boolean isError = false;
        try {
            filmController.removeLike(999, 1);
        } catch (ResourceNotFoundException e) {
            isError = true;
        }
        assertTrue(isError);
    }

}
