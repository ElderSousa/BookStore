package com.bookstore.jpa.Mappings.interfaces;

import org.mapstruct.Mapper;

import com.bookstore.jpa.dtos.records.Responses.ReviewResponse;
import com.bookstore.jpa.models.ReviewModel;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    ReviewResponse toDto(ReviewModel reviewModel);

}
