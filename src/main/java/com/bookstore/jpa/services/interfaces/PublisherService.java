package com.bookstore.jpa.services.interfaces;

import com.bookstore.jpa.dtos.records.Requests.PublisherResquest.PublisherCreateRequest;
import com.bookstore.jpa.dtos.records.Responses.PublisherResponse;

public interface PublisherService {

    PublisherResponse savePublisher(PublisherCreateRequest publisherRequest);
}
