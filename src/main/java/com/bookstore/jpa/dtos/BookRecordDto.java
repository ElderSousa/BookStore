package com.bookstore.jpa.dtos;

import java.util.Set;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BookRecordDto(
    @NotBlank(message = "O título não pode estar em branco ou ser nulo")
    @Size(min = 3, message = "O título deve ter no mínimo 3 caracteres")
    String title,

    @NotNull(message = "O ID da editora (publisherId) não pode ser nulo")
    UUID publisherId,

    @NotNull(message = "A lista de IDs de autores (authorIds) não pode ser nula")
    Set<UUID> authorIds,

    String reviewComment) {

}
