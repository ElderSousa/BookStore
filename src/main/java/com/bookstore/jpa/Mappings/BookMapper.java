package com.bookstore.jpa.Mappings;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.bookstore.jpa.dtos.Responses.AuthorResponseDto;
import com.bookstore.jpa.dtos.Responses.BookResponseDto;
import com.bookstore.jpa.models.BookModel;

@Component
public class BookMapper {

    private final AuthorMapper authorMapper;
    private final PublisherMapper publisherMapper;
    private final ReviewMapper reviewMapper;

    public BookMapper(AuthorMapper authorMapper, PublisherMapper publisherMapper, ReviewMapper reviewMapper) {
        this.authorMapper = authorMapper;
        this.publisherMapper = publisherMapper;
        this.reviewMapper = reviewMapper;
    }

    public BookResponseDto toDto(BookModel bookModel) {
        
        Set<AuthorResponseDto> authorsDto = bookModel.getAuthors()
            .stream()
            .map(authorMapper::toDto)
            .collect(Collectors.toSet());

        return new BookResponseDto(
            bookModel.getId(),
            bookModel.getTitle(),
            publisherMapper.toDto(bookModel.getPublisher()),
            authorsDto,                                      
            reviewMapper.toDto(bookModel.getReview()) 
        );
    }
}
