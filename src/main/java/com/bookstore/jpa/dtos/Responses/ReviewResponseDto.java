package com.bookstore.jpa.dtos.Responses;

import java.util.UUID;

public record ReviewResponseDto(
    UUID id,
    String comment
    ) {

}
