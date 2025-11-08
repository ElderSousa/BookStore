package com.bookstore.jpa.Mappings.interfaces;

import org.mapstruct.Mapper;

import com.bookstore.jpa.dtos.records.Responses.PublisherResponse;
import com.bookstore.jpa.models.PublisherModel;

@Mapper(componentModel = "spring")
public interface PublisherMapper {

    PublisherResponse toDto(PublisherModel publisherModel);

}
