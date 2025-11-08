package com.bookstore.jpa.Mappings.interfaces;

import org.mapstruct.Mapper;

import com.bookstore.jpa.dtos.records.Responses.AuthorResponse;
import com.bookstore.jpa.models.AuthorModel;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

       AuthorResponse toDto(AuthorModel authorModel);

}
