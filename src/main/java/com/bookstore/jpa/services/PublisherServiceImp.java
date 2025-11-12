package com.bookstore.jpa.services;

import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bookstore.jpa.Mappings.interfaces.PublisherMapper;
import com.bookstore.jpa.dtos.records.Requests.PublisherResquest.PublisherCreateRequest;
import com.bookstore.jpa.dtos.records.Responses.PublisherResponse;
import com.bookstore.jpa.models.PublisherModel;
import com.bookstore.jpa.repositories.PublisherRepository;
import com.bookstore.jpa.services.interfaces.PublisherService;

public class PublisherServiceImp implements PublisherService{

    private final PublisherRepository publisherRepository;
    private final PublisherMapper publisherMapper;
    private static final Logger log = LoggerFactory.getLogger(PublisherServiceImp.class);

    
    public PublisherServiceImp(PublisherRepository publisherRepository, PublisherMapper publisherMapper){
        this.publisherRepository = publisherRepository;
        this.publisherMapper = publisherMapper;
    }

    public PublisherResponse savePublisher(PublisherCreateRequest publisherRequest){
        log.info("Iniciando tentativa de salvar publisher como nome {}", publisherRequest.name());

        var publisher = new PublisherModel();
        publisher.setName(publisherRequest.name());
        publisher.setBooks(publisherRequest.books());

        var savedPublisher = publisherRepository.save(publisher);

        log.info("Publisher com ID: {} salvo com sucesso", savedPublisher.getId());

        return publisherMapper.toDto(savedPublisher);
    }
}
