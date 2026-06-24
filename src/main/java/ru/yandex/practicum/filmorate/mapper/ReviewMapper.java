package ru.yandex.practicum.filmorate.mapper;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.request.CreateReviewRequest;
import ru.yandex.practicum.filmorate.dto.request.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.dto.response.ReviewResponse;
import ru.yandex.practicum.filmorate.model.Review;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReviewMapper {

    public static Review toEntity(CreateReviewRequest request) {
        Review review = new Review();
        review.setContent(request.getContent());
        review.setPositive(request.getIsPositive());
        review.setUserId(request.getUserId());
        review.setFilmId(request.getFilmId());
        return review;
    }

    public static Review toEntity(UpdateReviewRequest request) {
        Review review = new Review();
        review.setId(request.getReviewId());
        review.setContent(request.getContent());
        review.setPositive(request.getIsPositive());
        review.setUserId(request.getUserId());
        review.setFilmId(request.getFilmId());
        return review;
    }

    public static ReviewResponse toResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setReviewId(review.getId());
        response.setPositive(review.isPositive());
        response.setContent(review.getContent());
        response.setUserId(review.getUserId());
        response.setFilmId(review.getFilmId());
        response.setUseful(review.getUseful());
        return response;
    }
}
