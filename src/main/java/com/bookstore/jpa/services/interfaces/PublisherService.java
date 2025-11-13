package com.bookstore.jpa.services.interfaces;

import java.util.List;
import java.util.UUID;

import com.bookstore.jpa.dtos.records.Requests.PublisherResquest.PublisherCreateRequest;
import com.bookstore.jpa.dtos.records.Requests.PublisherResquest.PublisherUpdateRequest;
import com.bookstore.jpa.dtos.records.Responses.PublisherResponse;

public interface PublisherService {

    PublisherResponse savePublisher(PublisherCreateRequest publisherRequest);
    List<PublisherResponse> getAllPublisher();
    PublisherResponse getPublisherById(UUID id);
    PublisherResponse updatePublisher(UUID id, PublisherUpdateRequest publisherRequest);
}
