package com.bookstore.jpa.Mappings.interfaces;

import org.mapstruct.Mapper;

import com.bookstore.jpa.dtos.records.Responses.BookResponse;
import com.bookstore.jpa.models.BookModel;

@Mapper(componentModel = "spring", uses = { 
        PublisherMapper.class, 
        AuthorMapper.class, 
        ReviewMapper.class 
    })
public interface BookMapper {

    BookResponse toDto(BookModel bookModel);

}
