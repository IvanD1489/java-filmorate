package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilmControllerTest {

    Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    FilmController filmController = new FilmController();

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
    public void isUpdatingFilm() throws ValidationException {

        Film film = new Film(1, "Test name", "Test desc", "1997-08-21", 120);
        filmController.create(film);

        film = new Film(1, "Test name updated", "Test desc updated", "2020-08-21", 150);
        filmController.update(film);

        List<Film> films = new ArrayList<>();
        films.add(film);
        String toCheckWith = gson.toJson(films);

        assertEquals(toCheckWith, gson.toJson(filmController.getFilms()));
    }

    @Test
    public void isValidatingFilms() throws ValidationException {
        boolean isError = false;
        Film film = new Film(1, "", "Test desc", "1997-08-21", 120);
        try {
            filmController.create(film);
        } catch (ValidationException e) {
            isError = true;
        }

        assertTrue(isError);
    }

}
