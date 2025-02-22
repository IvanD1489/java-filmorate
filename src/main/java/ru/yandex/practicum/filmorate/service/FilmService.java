package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Qualifier("filmDbStorage")
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public void addLike(long filmId, long userId) throws ResourceNotFoundException {
        if (filmStorage.getFilmById(filmId) == null) {
            log.error("Фильм с идентификатором {} не найден.", filmId);
            throw new ResourceNotFoundException("Фильм не найден");
        }
        if (userStorage.getUserById(userId) == null) {
            log.error("Пользователь с идентификатором {} не найден.", userId);
            throw new ResourceNotFoundException("Пользователь не найден");
        }

        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(long filmId, long userId) throws ResourceNotFoundException {
        if (filmStorage.getFilmById(filmId) == null) {
            log.error("Фильм с идентификатором {} не найден.", filmId);
            throw new ResourceNotFoundException("Фильм не найден");
        }
        if (userStorage.getUserById(userId) == null) {
            log.error("Пользователь с идентификатором {} не найден.", userId);
            throw new ResourceNotFoundException("Пользователь не найден");
        }

        filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> getTopFilms(int count) {
        return filmStorage.getTopFilms(count);
    }

    public void deleteAllFilms() {
        filmStorage.deleteAllFilms();
    }

    @SneakyThrows
    public Film addFilm(Film film) throws ValidationException, ResourceNotFoundException {
        if (film.getDuration() < 1) {
            String err = "Продолжительность фильма должна быть положительным числом";
            log.error("При создании фильма возникла ошибка: {}. Указанная длительность: {}", err, film.getDuration());
            throw new ValidationException(err);
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            String err = "Максимальная длина описания - 200 символов";
            log.error("При создании фильма возникла ошибка: {}. Указанная длина: {}", err, film.getDescription());
            throw new ValidationException(err);
        }
        if (film.getName() == null || film.getName().isEmpty()) {
            String err = "Название фильма не может быть пустым";
            log.error("При создании фильма возникла ошибка: {}", err);
            throw new ValidationException(err);
        }
        if (LocalDate.parse(film.getReleaseDate()).isBefore(LocalDate.parse("1895-12-28"))) {
            String err = "Год выпуска фильма должен быть позде 28 декабря 1895 года";
            log.error("При создании фильма возникла ошибка: {}. Указанная дата: {}", err, film.getReleaseDate());
            throw new ValidationException(err);
        }
        if(film.getMpa().getId() < 1 || film.getMpa().getId() > Genre.values().size()){
            String err = "Не удалось найти рейтинг";
            log.error("При создании фильма возникла ошибка: {}.", err);
            throw new ResourceNotFoundException(err);
        }

        for (Genre genre : film.getGenres()) {
            if (genre.getId() < 1 || genre.getId() > Genre.values().size()) {
                String err = "Не удалось найти жанр с ID: " + genre.getId();
                log.error("При создании фильма возникла ошибка: {}.", err);
                throw new ResourceNotFoundException(err);
            }
        }

        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) throws ResourceNotFoundException, ValidationException {
        if (film.getDuration() < 1) {
            String err = "Продолжительность фильма должна быть положительным числом";
            log.error("При обновлении фильма возникла ошибка: {}. Указанная длительность: {}", err, film.getDuration());
            throw new ValidationException(err);
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            String err = "Максимальная длина описания - 200 символов";
            log.error("При обновлении фильма возникла ошибка: {}. Указанная длина: {}", err, film.getDescription());
            throw new ValidationException(err);
        }
        if (film.getName() == null || film.getName().isEmpty()) {
            String err = "Название фильма не может быть пустым";
            log.error("При обновлении фильма возникла ошибка: {}", err);
            throw new ValidationException(err);
        }
        if (LocalDate.parse(film.getReleaseDate()).isBefore(LocalDate.parse("1895-12-28"))) {
            String err = "Год выпуска фильма должен быть позде 28 декабря 1895 года";
            log.error("При обновлении фильма возникла ошибка: {}. Указанная дата: {}", err, film.getReleaseDate());
            throw new ValidationException(err);
        }

        if (filmStorage.getFilmById(film.getId()) == null) {
            log.error("Фильм с идентификатором {} не найден.", film.getId());
            throw new ResourceNotFoundException("Фильм не найден");
        }
        if(film.getMpa().getId() < 1 || film.getMpa().getId() > Genre.values().size()){
            String err = "Не удалось найти рейтинг";
            log.error("При создании фильма возникла ошибка: {}.", err);
            throw new ResourceNotFoundException(err);
        }

        for (Genre genre : film.getGenres()) {
            if (genre.getId() < 1 || genre.getId() > Genre.values().size()) {
                String err = "Не удалось найти жанр с ID: " + genre.getId();
                log.error("При создании фильма возникла ошибка: {}.", err);
                throw new ResourceNotFoundException(err);
            }
        }

        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(long id) throws ResourceNotFoundException {
        Film film = filmStorage.getFilmById(id);
        if (film == null) {
            log.error("Фильм с идентификатором {} не найден.", id);
            throw new ResourceNotFoundException("Фильм не найден");
        }
        return film;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void deleteFilm(long id) throws ResourceNotFoundException {
        Film film = filmStorage.getFilmById(id);
        if (film == null) {
            log.error("Фильм с идентификатором {} не найден.", id);
            throw new ResourceNotFoundException("Фильм не найден");
        }
        filmStorage.deleteFilm(id);
    }
}