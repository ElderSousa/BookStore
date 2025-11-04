package com.bookstore.jpa.dtos.Responses;

import java.util.UUID;

public record PublisherResponseDto(
    UUID id,
    String name
    ) {

}
