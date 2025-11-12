package com.bookstore.jpa.dtos.records.Requests;

import java.util.Set;

import org.hibernate.validator.constraints.Length;

import com.bookstore.jpa.models.BookModel;

import jakarta.validation.constraints.NotBlank;

public record PublisherResquest() {

    public record PublisherCreateRequest(
        @NotBlank(message = "O nome não pode estar em branco")
        @Length(min = 3, message = "O nome deve ter no mínimo 3 caracteres")
        String name,
        Set<BookModel> books
    ){
        
    }

    public record PublisherUpdateRequest(
        @NotBlank(message = "O nome não pode estar em branco")
        @Length(min = 3, message = "O nome deve ter no mínimo 3 caracteres")
        String name      
    ){

    }
}
