package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.request.CreateReviewRequest;
import ru.yandex.practicum.filmorate.dto.request.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.dto.response.ReviewResponse;

import java.util.List;

public interface ReviewService {
    ReviewResponse create(CreateReviewRequest request);

    ReviewResponse update(UpdateReviewRequest request);

    void delete(long id);

    ReviewResponse findById(long id);

    List<ReviewResponse> findAll(Long filmId, int count);

    void addReaction(long reviewId, long userId, boolean isLike);

    void removeReaction(long reviewId, long userId);

    void removeDislike(long reviewId, long userId);
}
