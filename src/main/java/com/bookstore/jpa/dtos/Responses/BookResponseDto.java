package com.bookstore.jpa.dtos.Responses;

import java.util.Set;
import java.util.UUID;

public record BookResponseDto(
        UUID id,
        String title,
        PublisherResponseDto publisher,
        Set<AuthorResponseDto> author,
        ReviewResponseDto review
    ){

}
