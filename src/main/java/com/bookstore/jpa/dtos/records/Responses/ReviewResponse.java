package com.bookstore.jpa.dtos.records.Responses;

import java.util.UUID;

public record ReviewResponse(
    UUID id,
    String comment
    ) {

}
