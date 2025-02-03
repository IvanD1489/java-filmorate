package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    private FilmService filmService;

    @Autowired
    public FilmController(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("Запрос на получение списка всех фильмов.");
        return filmStorage.getAllFilms();
    }

    @PostMapping
    public Film create(@RequestBody Film film) throws ValidationException {
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

        filmStorage.addFilm(film);
        log.info("Создан новый фильм. Ему присвоен идентификатор: {}", film.getId());
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) throws ValidationException, ResourceNotFoundException {
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

        return filmStorage.updateFilm(film);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable long id) throws ResourceNotFoundException {
        Film film = filmStorage.getFilmById(id);
        if (film != null) {
            log.info("Фильм с идентификатором {} найден.", id);
            return film;
        } else {
            log.error("Фильм с идентификатором {} не найден.", id);
            throw new ResourceNotFoundException("Фильм не найден");
        }
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable long id) throws ResourceNotFoundException {
        if (filmStorage.getFilmById(id) == null) {
            log.error("Фильм с идентификатором {} не найден.", id);
            throw new ResourceNotFoundException("Фильм не найден");
        }
        filmStorage.deleteFilm(id);
        log.info("Фильм с идентификатором {} удалён.", id);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void addLike(@PathVariable long filmId, @PathVariable long userId) throws ResourceNotFoundException {
        if (filmStorage.getFilmById(filmId) == null) {
            log.error("Фильм с идентификатором {} не найден.", filmId);
            throw new ResourceNotFoundException("Фильм не найден");
        }
        if (userStorage.getUserById(userId) == null) {
            log.error("Пользователь с идентификатором {} не найден.", userId);
            throw new ResourceNotFoundException("Пользователь не найден");
        }
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void removeLike(@PathVariable long filmId, @PathVariable long userId) throws ResourceNotFoundException {
        if (filmStorage.getFilmById(filmId) == null) {
            log.error("Фильм с идентификатором {} не найден.", filmId);
            throw new ResourceNotFoundException("Фильм не найден");
        }
        if (userStorage.getUserById(userId) == null) {
            log.error("Пользователь с идентификатором {} не найден.", userId);
            throw new ResourceNotFoundException("Пользователь не найден");
        }
        filmService.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getTopFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getTopFilms(count);
    }

    public void deleteAllFilms() {
        filmService.deleteAllFilms();
    }

}
