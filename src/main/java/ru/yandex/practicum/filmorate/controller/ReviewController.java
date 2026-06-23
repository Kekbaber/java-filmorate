package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.request.CreateReviewRequest;
import ru.yandex.practicum.filmorate.dto.request.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.dto.response.ReviewResponse;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@Slf4j
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponse create(@Valid @RequestBody CreateReviewRequest request) {
        log.info("POST /reviews");
        ReviewResponse created = reviewService.create(request);
        log.info("Created review with id={}", created.getReviewId());
        return created;
    }

    @PutMapping
    public ReviewResponse update(@Valid @RequestBody UpdateReviewRequest request) {
        log.info("PUT /reviews: id={}", request.getReviewId());
        ReviewResponse updated = reviewService.update(request);
        log.info("Updated review with id={}", updated.getReviewId());
        return updated;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive long id) {
        log.info("DELETE /reviews/{}", id);
        reviewService.delete(id);
    }

    @GetMapping("/{id}")
    public ReviewResponse findById(@PathVariable long id) {
        log.info("GET /reviews/{}", id);
        return reviewService.findById(id);
    }

    @GetMapping
    public List<ReviewResponse> findAll(
            @RequestParam(required = false) Long filmId,
            @RequestParam(defaultValue = "10") @Positive int count) {
        log.debug("GET /reviews?filmId={}&count={}", filmId, count);
        List<ReviewResponse> reviews = reviewService.findAll(filmId, count);
        log.debug("Returned {} reviews", reviews.size());
        return reviews;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(
            @PathVariable @Positive long id,
            @PathVariable @Positive long userId) {
        log.info("PUT /reviews/{}/like/{}", id, userId);
        reviewService.addReaction(id, userId, true);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(
            @PathVariable @Positive long id,
            @PathVariable @Positive long userId) {
        log.info("PUT /reviews/{}/dislike/{}", id, userId);
        reviewService.addReaction(id, userId, false);
    }

    // Удаляет любую реакцию - условия ТЗ
    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(
            @PathVariable @Positive long id,
            @PathVariable @Positive long userId) {
        log.info("DELETE /reviews/{}/like/{}", id, userId);
        reviewService.removeReaction(id, userId);
    }

    // Удаляет только дизлайки
    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(
            @PathVariable @Positive long id,
            @PathVariable @Positive long userId) {
        log.info("DELETE /reviews/{}/dislike/{}", id, userId);
        reviewService.removeDislike(id, userId);
    }
}
