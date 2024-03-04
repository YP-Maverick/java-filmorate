package ru.yandex.practicum.filmorate;


import com.google.gson.Gson;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {

        Film film = Film.builder()
                .id(0)
                .description("Фильмец")
                .name("Диалоги Торантино")
                //.duration(Duration.ofHours(2).plus(Duration.ofMinutes(33)))
                .build();

        Gson gson = new Gson();

        System.out.println(gson.toJson(film));
    }

    public void validM(@Valid User user) {

    }
}