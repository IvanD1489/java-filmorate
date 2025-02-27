package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    @GetMapping
    public List<Genre> getGenres() {
        return Genre.values();
    }

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable int id) throws ResourceNotFoundException {
        if (id < 1 || id > Genre.values().size()) {
            throw new ResourceNotFoundException("Жанр не найден");
        }
        return Genre.values().get(id - 1);
    }

}
