package com.bookstore.jpa.dtos.records.Requests;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;

public record PublisherResquest() {

    public record PublisherCreateRequest(
        @NotBlank(message = "O nome não pode estar em branco")
        @Length(min = 3, message = "O nome deve ter no mínimo 3 caracteres")
        String name
    ){
        
    }

    public record PublisherUpdateRequest(
        @NotBlank(message = "O nome não pode estar em branco")
        @Length(min = 3, message = "O nome deve ter no mínimo 3 caracteres")
        String name      
    ){

    }
}
