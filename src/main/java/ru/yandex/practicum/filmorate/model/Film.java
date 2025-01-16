package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Film {

    private long id;
    private String name;
    private String description;
    private String releaseDate;
    private int duration;

}
