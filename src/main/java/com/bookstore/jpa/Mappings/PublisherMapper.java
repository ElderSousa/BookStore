package com.bookstore.jpa.Mappings;

import org.springframework.stereotype.Component;

import com.bookstore.jpa.dtos.Responses.PublisherResponseDto;
import com.bookstore.jpa.models.PublisherModel;

@Component
public class PublisherMapper {

    public PublisherResponseDto toDto(PublisherModel publisher) {
        if (publisher == null) {
            return null;
        }
        return new PublisherResponseDto(
           publisher.getId(),
           publisher.getName()
        );
    }
}
