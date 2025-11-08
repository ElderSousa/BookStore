package com.bookstore.jpa.dtos.records.Responses;

import java.util.UUID;

public record AuthorResponse(
        UUID id,
        String name
    ) {

}
