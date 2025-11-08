package com.bookstore.jpa.dtos.records.Responses;

import java.util.UUID;

public record PublisherResponse(
    UUID id,
    String name
    ) {

}
