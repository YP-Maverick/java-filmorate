package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Jacksonized
@Data
@Builder
public class User {

    private Long id;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Login must not be blank")
    @Pattern(regexp = "\\S+", message = "Login cannot contain spaces")
    private String login;

    private String name;

    @PastOrPresent(message = "Birthday date must be in the past")
    private LocalDate birthday;

    @Builder.Default
    private Set<Long> friendsId = new HashSet<>();
}