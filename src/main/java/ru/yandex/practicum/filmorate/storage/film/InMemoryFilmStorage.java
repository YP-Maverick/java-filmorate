package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private static final List<Film> films = new ArrayList<>(); // Пока нет БД храним в контроллере

    @Override
    public void add(Film film) {
        films.add(film);
    }

    @Override
    public Film getFilmById(Long filmId) {
        return films.stream()
                .filter(film -> film.getId().equals(filmId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Film> getAllFilms() {
        return films;
    }
}
