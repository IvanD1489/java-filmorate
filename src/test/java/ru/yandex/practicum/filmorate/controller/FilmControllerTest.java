package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
    public void isCreatingAndGettingFilms() throws ValidationException {

        Film film = new Film(1, "Test name", "Test desc", "1997-08-21", 120);
        List<Film> films = new ArrayList<>();
        films.add(film);
        String toCheckWith = gson.toJson(films);

        filmController.create(film);

        assertEquals(toCheckWith, gson.toJson(filmController.getFilms()));
    }

    @Test
    public void isUpdatingFilm() throws ValidationException, ResourceNotFoundException {

        Film film = new Film(1, "Test name", "Test desc", "1997-08-21", 120);
        filmController.create(film);

        film = new Film(1, "Test name updated", "Test desc updated", "2020-08-21", 150);
        filmController.update(film);

        String toCheckWith = gson.toJson(film);

        assertEquals(toCheckWith, gson.toJson(filmController.getFilmById(film.getId())));
    }

    @Test
    public void isValidatingFilms() {
        boolean isError = false;
        Film film = new Film(1, "", "Test desc", "1997-08-21", 120);
        try {
            filmController.create(film);
        } catch (ValidationException e) {
            isError = true;
        }

        assertTrue(isError);
    }

    @Test
    public void isGettingTopFilms() throws ValidationException, ResourceNotFoundException {
        Film film1 = new Film(1, "Film 1", "Description 1", "2020-01-01", 120);
        Film film2 = new Film(2, "Film 2", "Description 2", "2020-01-02", 150);
        Film film3 = new Film(3, "Film 3", "Description 3", "2020-01-03", 100);

        User user1 = new User(1, "test1@ya.ru", "testLogin1", "Test user1", "1997-08-21", null);
        userController.create(user1);
        User user2 = new User(2, "test2@ya.ru", "testLogin2", "Test user2", "1997-08-21", null);
        userController.create(user2);
        User user3 = new User(3, "test3@ya.ru", "testLogin3", "Test user3", "1997-08-21", null);
        userController.create(user3);

        filmController.create(film1);
        filmController.create(film2);
        filmController.create(film3);

        filmController.addLike(1, 1);
        filmController.addLike(2, 1);
        filmController.addLike(2, 2);
        filmController.addLike(3, 2);
        filmController.addLike(3, 3);


        List<Film> topFilms = filmController.getTopFilms(2);
        List<Film> expectedTopFilms = List.of(film3, film2);

        assertEquals(expectedTopFilms.size(), topFilms.size());
        assertTrue(topFilms.containsAll(expectedTopFilms));
    }

    @Test
    public void isRemovingLike() throws ValidationException, ResourceNotFoundException {
        Film film = new Film(1, "Test Film", "Test desc", "2020-01-01", 120);
        filmController.create(film);

        User user = new User(1, "test@ya.ru", "testLogin", "Test user", "1997-08-21", null);
        userController.create(user);

        filmController.addLike(1, 1);

        List<Film> topFilmsBeforeRemoval = filmController.getTopFilms(1);
        assertEquals(1, topFilmsBeforeRemoval.size());

        filmController.removeLike(1, 1);

        List<Film> topFilmsAfterRemoval = filmController.getTopFilms(1);
        assertTrue(topFilmsAfterRemoval.isEmpty());
    }

    @Test
    public void isDeletingFilm() throws ValidationException, ResourceNotFoundException {
        Film film = new Film(1, "Film to delete", "Description", "2020-01-01", 120);
        filmController.create(film);

        assertNotNull(filmController.getFilmById(film.getId()));

        filmController.deleteFilm(1);

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
