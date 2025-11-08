package com.bookstore.jpa.dtos.records.Responses;

import java.util.Set;
import java.util.UUID;

public record BookResponse(
        UUID id,
        String title,
        PublisherResponse publisher,
        Set<AuthorResponse> author,
        ReviewResponse review
    ){

}
