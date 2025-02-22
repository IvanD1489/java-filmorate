package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rating {

    private int id;
    private String name;

    public static List<Rating> values() {
        return Arrays.asList(
                new Rating(1, "G"),
                new Rating(2, "PG"),
                new Rating(3, "PG-13"),
                new Rating(4, "R"),
                new Rating(5, "NC-17")
        );
    }

}
