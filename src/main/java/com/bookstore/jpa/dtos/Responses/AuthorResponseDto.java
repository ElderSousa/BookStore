package com.bookstore.jpa.dtos.Responses;

import java.util.UUID;

public record AuthorResponseDto(
        UUID id,
        String name
    ) {

}
