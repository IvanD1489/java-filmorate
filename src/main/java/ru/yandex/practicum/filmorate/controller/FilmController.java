package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    private final static Logger log = LoggerFactory.getLogger(FilmController.class);

    @GetMapping
    public List<Film> getFilms() {
        log.info("Пользователь запросил список всех фильмов.");
        return new ArrayList<>(films.values());
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

        film.setId(getNextId());
        films.put(film.getId(), film);

        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) throws ValidationException {
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

        Film filmToUpdate = films.get(film.getId());
        if (filmToUpdate == null) {
            String err = "Год выпуска фильма должен быть позде 28 декабря 1895 года";
            log.error("При обновлении фильма возникла ошибка: {}. Указанная дата: {}", err, film.getReleaseDate());
            throw new ValidationException("Пользователь не найден");
        }
        filmToUpdate.setName(film.getName());
        filmToUpdate.setDescription(film.getDescription());
        filmToUpdate.setDuration(film.getDuration());
        filmToUpdate.setReleaseDate(film.getReleaseDate());

        return filmToUpdate;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
