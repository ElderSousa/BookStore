package com.bookstore.jpa.Mappings;

import org.springframework.stereotype.Component;

import com.bookstore.jpa.dtos.Responses.AuthorResponseDto;
import com.bookstore.jpa.models.AuthorModel;

@Component
public class AuthorMapper {

    public AuthorResponseDto toDto(AuthorModel author) { // <-- A ASSINATURA CORRETA
        if (author == null) {
            return null;
        }
        
        return new AuthorResponseDto(
            author.getId(),
            author.getName()
        );
    }
}
