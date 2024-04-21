package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.validator.ValidFilmReleaseDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Jacksonized
@Data
@Builder
public class Film {
    private Long id;

    @NotBlank(message = "Name should not be blank")
    @NotEmpty(message = "Name should not be empty")
    private String name;

    @Length(max = 200, message = "Description len must be greater than 200")
    private String description;

    // Пользовательская аннотация валидации
    @ValidFilmReleaseDate(message = "Invalid film release date", fistFilmDate = "1895-12-28")
    private LocalDate releaseDate;

    @Positive(message = "must be greater than 0")
    private int duration;

    @Builder.Default
    private Set<Long> likedUsersId = new HashSet<>();

    private Mpa mpa;

    private Set<Genre> genres;
}
