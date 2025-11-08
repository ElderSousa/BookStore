package com.bookstore.jpa.dtos.records.Requests;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record AuthorAssociationRequest(
    @NotNull(message = "O authorId não pode ser nulo")
    UUID authorId
    ) {

}
