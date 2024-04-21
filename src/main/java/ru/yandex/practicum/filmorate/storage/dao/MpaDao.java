package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaDao {

    Mpa getMpaById(Long mpaId);

    Mpa getMpaByFilmId(Long filmId);

    List<Mpa> getAllMpa();

    Boolean checkMpaId(Long id);
}