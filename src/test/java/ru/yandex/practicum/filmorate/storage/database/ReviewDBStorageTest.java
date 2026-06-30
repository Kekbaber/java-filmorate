package ru.yandex.practicum.filmorate.storage.database;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.yandex.practicum.filmorate.exception.model.InternalServerException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.db.ReviewDBStorage;
import ru.yandex.practicum.filmorate.storage.db.mappers.ReviewRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({ReviewDBStorage.class, ReviewRowMapper.class})
class ReviewDBStorageTest {

    @Autowired
    private final ReviewStorage reviewStorage;

    @Autowired
    private final JdbcTemplate jdbc;

    private long createTestFilm(String name) {
        String sql = "INSERT INTO films (name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, "Test description");
            ps.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
            ps.setInt(4, 120);
            ps.setInt(5, 1);
            return ps;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    private long createTestUser(String email, String login) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, email);
            ps.setString(2, login);
            ps.setString(3, login);
            ps.setDate(4, java.sql.Date.valueOf(LocalDate.of(2000, 1, 1)));
            return ps;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    private Review createTestReview(String content, boolean isPositive, long userId, long filmId) {
        Review review = new Review();
        review.setContent(content);
        review.setPositive(isPositive);
        review.setUserId(userId);
        review.setFilmId(filmId);
        return reviewStorage.create(review);
    }

    @BeforeEach
    void cleanTables() {
        jdbc.execute("DELETE FROM review_likes");
        jdbc.execute("DELETE FROM reviews");
        jdbc.execute("DELETE FROM likes");
        jdbc.execute("DELETE FROM friendships");
        jdbc.execute("DELETE FROM film_genre");
        jdbc.execute("DELETE FROM films");
        jdbc.execute("DELETE FROM users");
        jdbc.execute("ALTER TABLE reviews ALTER COLUMN id RESTART WITH 1");
        jdbc.execute("ALTER TABLE films ALTER COLUMN id RESTART WITH 1");
        jdbc.execute("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");
    }

    @Test
    void findAll_WhenEmpty_ShouldReturnEmpty() {
        List<Review> reviews = reviewStorage.findAll(10);
        assertThat(reviews).isEmpty();
    }

    @Test
    void create_ShouldGenerateIdAndSaveReview() {
        long filmId = createTestFilm("Film");
        long userId = createTestUser("user@mail.ru", "user");
        Review created = createTestReview("Great film!", true, userId, filmId);

        assertThat(created.getId()).isPositive();
        assertThat(created.getContent()).isEqualTo("Great film!");
        assertThat(created.isPositive()).isTrue();
        assertThat(created.getUserId()).isEqualTo(userId);
        assertThat(created.getFilmId()).isEqualTo(filmId);
        assertThat(created.getUseful()).isZero();
    }

    @Test
    void findById_WhenExists_ShouldReturnReview() {
        long filmId = createTestFilm("Film");
        long userId = createTestUser("user@mail.ru", "user");
        Review saved = createTestReview("Nice", true, userId, filmId);

        Optional<Review> found = reviewStorage.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getContent()).isEqualTo("Nice");
    }

    @Test
    void findById_WhenNotFound_ShouldReturnEmpty() {
        Optional<Review> found = reviewStorage.findById(999L);
        assertThat(found).isEmpty();
    }

    @Test
    void findAll_ShouldReturnAllReviewsOrderedByUseful() {
        long filmId = createTestFilm("Film");
        long user1 = createTestUser("u1@mail.ru", "u1");
        long user2 = createTestUser("u2@mail.ru", "u2");
        Review r1 = createTestReview("First", true, user1, filmId);
        Review r2 = createTestReview("Second", false, user2, filmId);

        reviewStorage.addReaction(r1.getId(), user2, true);

        List<Review> all = reviewStorage.findAll(10);
        assertThat(all).hasSize(2);
        assertThat(all.get(0).getId()).isEqualTo(r1.getId());
        assertThat(all.get(1).getId()).isEqualTo(r2.getId());
    }

    @Test
    void findByFilmId_ShouldReturnReviewsForGivenFilm() {
        long film1 = createTestFilm("Film1");
        long film2 = createTestFilm("Film2");
        long user = createTestUser("u@mail.ru", "u");
        createTestReview("For film1", true, user, film1);
        createTestReview("For film2", true, user, film2);

        List<Review> reviews = reviewStorage.findByFilmId(film1, 10);
        assertThat(reviews).hasSize(1);
        assertThat(reviews.getFirst().getContent()).isEqualTo("For film1");
    }

    @Test
    void findByFilmId_WhenNoReviews_ShouldReturnEmpty() {
        long filmId = createTestFilm("Lonely film");
        List<Review> reviews = reviewStorage.findByFilmId(filmId, 10);
        assertThat(reviews).isEmpty();
    }

    @Test
    void update_ShouldChangeFields() {
        long filmId = createTestFilm("Film");
        long userId = createTestUser("u@mail.ru", "u");
        Review saved = createTestReview("Old", true, userId, filmId);

        saved.setContent("New content");
        saved.setPositive(false);
        Review updated = reviewStorage.update(saved);

        assertThat(updated.getContent()).isEqualTo("New content");
        assertThat(updated.isPositive()).isFalse();
    }

    @Test
    void update_WhenReviewNotExists_ShouldThrowInternalServerException() {
        Review ghost = new Review();
        ghost.setId(999L);
        ghost.setContent("Ghost");
        ghost.setPositive(true);
        ghost.setUserId(1L);
        ghost.setFilmId(1L);

        assertThatThrownBy(() -> reviewStorage.update(ghost))
                .isInstanceOf(InternalServerException.class);
    }

    @Test
    void delete_ShouldRemoveReview() {
        long filmId = createTestFilm("Film");
        long userId = createTestUser("u@mail.ru", "u");
        Review saved = createTestReview("Delete me", true, userId, filmId);

        reviewStorage.delete(saved.getId());
        assertThat(reviewStorage.findById(saved.getId())).isEmpty();
    }

    @Test
    void delete_WhenReviewNotExists_ShouldDoNothing() {
        reviewStorage.delete(999L);
        assertThat(reviewStorage.findAll(10)).isEmpty();
    }

    @Test
    void addReaction_WithLike_ShouldIncreaseUseful() {
        long filmId = createTestFilm("Film");
        long author = createTestUser("author@mail.ru", "author");
        long voter = createTestUser("voter@mail.ru", "voter");
        Review review = createTestReview("Review", true, author, filmId);

        reviewStorage.addReaction(review.getId(), voter, true);

        Review found = reviewStorage.findById(review.getId()).orElseThrow();
        assertThat(found.getUseful()).isEqualTo(1);
    }

    @Test
    void addReaction_WithDislike_ShouldDecreaseUseful() {
        long filmId = createTestFilm("Film");
        long author = createTestUser("author@mail.ru", "author");
        long voter = createTestUser("voter@mail.ru", "voter");
        Review review = createTestReview("Review", true, author, filmId);

        reviewStorage.addReaction(review.getId(), voter, false);

        Review found = reviewStorage.findById(review.getId()).orElseThrow();
        assertThat(found.getUseful()).isEqualTo(-1);
    }

    @Test
    void addReaction_WhenSwitchingFromLikeToDislike_ShouldUpdateUseful() {
        long filmId = createTestFilm("Film");
        long author = createTestUser("author@mail.ru", "author");
        long voter = createTestUser("voter@mail.ru", "voter");
        Review review = createTestReview("Review", true, author, filmId);

        reviewStorage.addReaction(review.getId(), voter, true);
        assertThat(reviewStorage.findById(review.getId()).orElseThrow().getUseful()).isEqualTo(1);

        reviewStorage.addReaction(review.getId(), voter, false);
        assertThat(reviewStorage.findById(review.getId()).orElseThrow().getUseful()).isEqualTo(-1);
    }

    @Test
    void addReaction_WhenReviewNotExists_ShouldThrowException() {
        long userId = createTestUser("u@mail.ru", "u");
        assertThatThrownBy(() -> reviewStorage.addReaction(999L, userId, true))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void addReaction_WhenUserNotExists_ShouldThrowException() {
        long filmId = createTestFilm("Film");
        long author = createTestUser("author@mail.ru", "author");
        Review review = createTestReview("Review", true, author, filmId);
        long reviewId = review.getId();
        assertThatThrownBy(() -> reviewStorage.addReaction(reviewId, 999L, true))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void deleteReaction_ShouldRemoveAnyReaction() {
        long filmId = createTestFilm("Film");
        long author = createTestUser("author@mail.ru", "author");
        long voter = createTestUser("voter@mail.ru", "voter");
        Review review = createTestReview("Review", true, author, filmId);

        reviewStorage.addReaction(review.getId(), voter, true);
        assertThat(reviewStorage.findById(review.getId()).orElseThrow().getUseful()).isEqualTo(1);

        reviewStorage.deleteReaction(review.getId(), voter);
        assertThat(reviewStorage.findById(review.getId()).orElseThrow().getUseful()).isZero();
    }

    @Test
    void deleteReactionByType_ShouldRemoveOnlyDislike() {
        long filmId = createTestFilm("Film");
        long author = createTestUser("author@mail.ru", "author");
        long voter = createTestUser("voter@mail.ru", "voter");
        Review review = createTestReview("Review", true, author, filmId);

        reviewStorage.addReaction(review.getId(), voter, false);
        assertThat(reviewStorage.findById(review.getId()).orElseThrow().getUseful()).isEqualTo(-1);

        reviewStorage.deleteReactionByType(review.getId(), voter, false);
        assertThat(reviewStorage.findById(review.getId()).orElseThrow().getUseful()).isZero();
    }

    @Test
    void deleteReaction_WhenNoReaction_ShouldDoNothing() {
        long filmId = createTestFilm("Film");
        long author = createTestUser("author@mail.ru", "author");
        Review review = createTestReview("Review", true, author, filmId);

        reviewStorage.deleteReaction(review.getId(), 999L);

        assertThat(reviewStorage.findById(review.getId()).orElseThrow().getUseful()).isZero();
    }

    @Test
    void useful_WithMultipleReactions_ShouldComputeCorrectly() {
        long filmId = createTestFilm("Film");
        long author = createTestUser("author@mail.ru", "author");
        long u1 = createTestUser("u1@mail.ru", "u1");
        long u2 = createTestUser("u2@mail.ru", "u2");
        long u3 = createTestUser("u3@mail.ru", "u3");
        Review review = createTestReview("Review", true, author, filmId);

        reviewStorage.addReaction(review.getId(), u1, true);
        reviewStorage.addReaction(review.getId(), u2, true);
        reviewStorage.addReaction(review.getId(), u3, true);
        reviewStorage.addReaction(review.getId(), author, false);

        Review found = reviewStorage.findById(review.getId()).orElseThrow();
        assertThat(found.getUseful()).isEqualTo(2);
    }
}