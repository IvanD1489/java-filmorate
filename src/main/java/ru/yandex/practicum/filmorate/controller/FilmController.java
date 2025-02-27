package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public List<Film> getFilms() {
        log.info("Запрос на получение списка всех фильмов.");
        return filmService.getAllFilms();
    }

    @PostMapping
    public Film create(@RequestBody Film film) throws ValidationException, ResourceNotFoundException {
        filmService.addFilm(film);
        log.info("Создан новый фильм. Ему присвоен идентификатор: {}", film.getId());
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) throws ValidationException, ResourceNotFoundException {
        return filmService.updateFilm(film);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable long id) throws ResourceNotFoundException {
        return filmService.getFilmById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable long id) throws ResourceNotFoundException {
        filmService.deleteFilm(id);
        log.info("Фильм с идентификатором {} удалён.", id);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void addLike(@PathVariable long filmId, @PathVariable long userId) throws ResourceNotFoundException {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void removeLike(@PathVariable long filmId, @PathVariable long userId) throws ResourceNotFoundException {
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
