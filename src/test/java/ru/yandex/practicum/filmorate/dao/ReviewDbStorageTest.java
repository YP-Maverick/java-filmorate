package ru.yandex.practicum.filmorate.dao;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.ReviewDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.dao.mapper.ModelMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;


import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Slf4j
@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewDbStorageTest {

    private static ModelMapper modelMapper;
    private final JdbcTemplate jdbcTemplate;

    private ReviewStorage reviewStorage;
    private FilmStorage filmStorage;
    private UserStorage userStorage;

    @BeforeAll
    public static void beforeAll() {
        modelMapper = new ModelMapper();
    }

    @BeforeEach
    public void beforeEach() {
        reviewStorage = new ReviewDbStorage(jdbcTemplate, modelMapper);
        filmStorage = new FilmDbStorage(jdbcTemplate, modelMapper);
        userStorage = new UserDbStorage(jdbcTemplate, modelMapper);
    }

    private User createUser(String email, String login, String name, LocalDate birthday) {
        User user = User.builder().id(0L).email(email).login(login).name(name).birthday(birthday).build();
        return userStorage.create(user);
    }

    private Film createFilm(String name, String description, LocalDate releaseDate, int duration, int mpaId) {
        Film film = Film.builder().id(0L).name(name).description(description).releaseDate(releaseDate)
                .duration(duration).mpa(RatingMpa.builder().id(mpaId).build()).build();
        return filmStorage.create(film);
    }

    @Test
    public void testCreateAndGetReviewFlow() {
        User user1 = createUser("user1@example.com", "user1", "Alice", LocalDate.of(1990, 5, 15));
        Film film1 = createFilm("Inception", "A thief who enters the dreams of others to steal their secrets",
                LocalDate.of(2010, 7, 16), 148, 2);

        Review review = Review.builder().content("Great Movie!").isPositive(true).userId(user1.getId()).filmId(film1.getId()).build();
        Review createdReview = reviewStorage.createReview(review);
        assertNotNull(createdReview);
        assertNotNull(createdReview.getId());
        assertEquals(review.getContent(), createdReview.getContent());

        Review foundReview = reviewStorage.getReview(createdReview.getId());
        assertNotNull(foundReview);
        assertEquals("Great Movie!", foundReview.getContent());
    }

    @Test
    public void testUpdateAndDeleteReview() {
        User user2 = createUser("user2@example.com", "user2", "Bob", LocalDate.of(1992, 8, 23));
        Film film2 = createFilm("Avatar", "A marine on an alien planet", LocalDate.of(2009, 12, 18), 162, 3);

        Review review = Review.builder().content("Amazing visuals").isPositive(true).userId(user2.getId()).filmId(film2.getId()).build();
        Review createdReview = reviewStorage.createReview(review);

        Review updateInfo = Review.builder().id(createdReview.getId()).content("Updated content").isPositive(false).build();
        Review updatedReview = reviewStorage.updateReview(updateInfo);

        assertNotNull(updatedReview);
        assertEquals("Updated content", updatedReview.getContent());

        reviewStorage.deleteReview(updatedReview.getId());
        assertEquals(Collections.EMPTY_LIST, reviewStorage.getAllReviews());
    }

    @Test
    public void testGetAllReviews() {
        User user3 = createUser("user3@example.com", "user3", "Charlie", LocalDate.of(1985, 9, 19));
        Film film3 = createFilm("The Matrix", "A hacker learns about the true nature of his reality and his role in the war against its controllers",
                LocalDate.of(1999, 3, 31), 136, 4);

        Review review1 = Review.builder().content("Great Movie!").isPositive(true).userId(user3.getId()).filmId(film3.getId()).build();
        Review review2 = Review.builder().content("Bad Movie!").isPositive(false).userId(user3.getId()).filmId(film3.getId()).build();
        Review review3 = Review.builder().content("Awesome!").isPositive(true).userId(user3.getId()).filmId(film3.getId()).build();

        List<Review> reviews = List.of(review1, review2, review3);

        reviews.forEach(review -> {
            Review createdReview = reviewStorage.createReview(review);
            review.withId(createdReview.getId());
        });

        assertEquals(reviews.size(), reviewStorage.getAllReviews().size());
    }
}