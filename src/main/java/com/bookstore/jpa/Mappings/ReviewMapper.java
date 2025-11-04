package com.bookstore.jpa.Mappings;

import org.springframework.stereotype.Component;

import com.bookstore.jpa.dtos.Responses.ReviewResponseDto;
import com.bookstore.jpa.models.ReviewModel;

@Component
public class ReviewMapper {

    public ReviewResponseDto toDto(ReviewModel review) {
        if (review == null) {
            return null;
        }
        return new ReviewResponseDto(
            review.getId(),
            review.getComment()
        );
    }
}
