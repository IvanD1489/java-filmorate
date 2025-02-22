package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private final Map<Long, Set<Long>> filmLikes = new HashMap<>();

    @Override
    public Film addFilm(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film getFilmById(long id) {
        return films.get(id);
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public void deleteFilm(long id) {
        films.remove(id);
    }

    @Override
    public void deleteAllFilms() {
        films.clear();
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        filmLikes.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        Set<Long> likes = filmLikes.get(filmId);
        if (likes != null) {
            likes.remove(userId);
            if (likes.isEmpty()) {
                filmLikes.remove(filmId);
            }
        }
    }

    @Override
    public List<Film> getTopFilms(int count) {
        return filmLikes.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue().size(), e1.getValue().size()))
                .limit(count)
                .map(Map.Entry::getKey)
                .map(this::getFilmById)
                .collect(Collectors.toList());
    }

    private long getNextId() {
        return films.keySet().stream().mapToLong(id -> id).max().orElse(0) + 1;
    }
}