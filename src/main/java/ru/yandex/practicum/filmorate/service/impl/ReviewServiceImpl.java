package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dto.request.CreateReviewRequest;
import ru.yandex.practicum.filmorate.dto.request.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.dto.response.ReviewResponse;
import ru.yandex.practicum.filmorate.exception.model.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewStorage reviewStorage;
    private final FilmService filmService;
    private final UserService userService;

    @Override
    @Transactional
    public ReviewResponse create(CreateReviewRequest request) {
        log.debug("Create review: filmId={}, userId={}, isPositive={}",
                request.getFilmId(), request.getUserId(), request.getIsPositive());
        filmService.findById(request.getFilmId());
        userService.findById(request.getUserId());
        Review review = ReviewMapper.toEntity(request);
        Review created = reviewStorage.create(review);
        log.debug("Created review with id={}", created.getId());
        return ReviewMapper.toResponse(created);
    }

    @Override
    @Transactional
    public ReviewResponse update(UpdateReviewRequest request) {
        log.debug("Update review: id={}", request.getReviewId());
        findById(request.getReviewId());
        filmService.findById(request.getFilmId());
        userService.findById(request.getUserId());
        Review review = ReviewMapper.toEntity(request);
        Review updated = reviewStorage.update(review);
        log.debug("Updated review with id={}", updated.getId());
        return ReviewMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void delete(long id) {
        log.debug("Delete review: id={}", id);
        findById(id);
        reviewStorage.delete(id);
        log.debug("Deleted review with id={}", id);
    }

    @Override
    public ReviewResponse findById(long id) {
        log.debug("Find review by id={}", id);
        return reviewStorage.findById(id)
                .map(ReviewMapper::toResponse)
                .orElseThrow(
                        () -> new NotFoundException("Ревью с id: " + id + " не найдено")
                );
    }

    @Override
    public List<ReviewResponse> findAll(Long filmId, int count) {
        log.debug("Find reviews by filmId={}, count={}", filmId, count);
        if (filmId != null) {
            log.debug("filmId is null");
            return reviewStorage.findByFilmId(filmId, count).stream()
                    .map(ReviewMapper::toResponse)
                    .toList();
        }
        List<Review> responses = reviewStorage.findAll(count);
        log.debug("Found {} reviews", responses.size());
        return responses.stream()
                .map(ReviewMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void addReaction(long reviewId, long userId, boolean isLike) {
        log.debug("Add reaction to review: reviewId={}, userId={}, isLike{}", reviewId, userId, isLike);
        findById(reviewId);
        userService.findById(userId);
        reviewStorage.addReaction(reviewId, userId, isLike);
    }

    @Override
    @Transactional
    public void removeReaction(long reviewId, long userId) {
        log.debug("Remove reaction to review: reviewId={}, userId={}", reviewId, userId);
        findById(reviewId);
        userService.findById(userId);
        reviewStorage.deleteReaction(reviewId, userId);
    }

    @Override
    @Transactional
    public void removeDislike(long reviewId, long userId) {
        log.debug("Remove dislike to review: reviewId={}, userId={}", reviewId, userId);
        findById(reviewId);
        userService.findById(userId);
        reviewStorage.deleteReactionByType(reviewId, userId, false);
    }
}
