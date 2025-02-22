package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class RatingController {

    @GetMapping
    public List<Rating> getRatings() {
        return Rating.values();
    }

    @GetMapping("/{id}")
    public Rating getRatingById(@PathVariable int id) throws ResourceNotFoundException {
        if(id < 1 || id > Rating.values().size()){
            throw new ResourceNotFoundException("Жанр не найден");
        }
        return Rating.values().get(id - 1);
    }

}
